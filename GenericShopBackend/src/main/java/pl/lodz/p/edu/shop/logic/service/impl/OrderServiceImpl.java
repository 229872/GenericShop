package pl.lodz.p.edu.shop.logic.service.impl;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Order;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Product;
import pl.lodz.p.edu.shop.dataaccess.repository.api.OrderRepository;
import pl.lodz.p.edu.shop.dataaccess.repository.api.ProductRepository;
import pl.lodz.p.edu.shop.dataaccess.repository.api.ReadOnlyAccountRepository;
import pl.lodz.p.edu.shop.exception.ApplicationExceptionFactory;
import pl.lodz.p.edu.shop.exception.SystemExceptionFactory;
import pl.lodz.p.edu.shop.logic.service.api.OrderService;
import pl.lodz.p.edu.shop.util.ExceptionUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

@Service
@Transactional(transactionManager = "ordersModTxManager", propagation = Propagation.REQUIRES_NEW)
@Qualifier("OrderServiceImpl")
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ReadOnlyAccountRepository accountRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, ReadOnlyAccountRepository accountRepository) {
        requireNonNull(orderRepository, "OrderService requires non null orderRepository");
        requireNonNull(productRepository, "OrderService requires non null productRepository");
        requireNonNull(accountRepository, "OrderService requires non null accountRepository");

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public Order placeAndOrder(String login, Map<Long, Integer> requestedProductsForOrder) {
        // HELPERS
        Set<Long> productsId = requestedProductsForOrder.keySet();
        Predicate<Product> isProductAvailableForBeingOrdered = product -> {
            Integer quantity = product.getQuantity();
            return !product.isArchival() && productsId.contains(product.getId()) && quantity > 0;
        };


        // LOGIC
        Account account = accountRepository.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

        List<Product> products = productRepository.findAll();

        Map<Product, Integer> productsForNewOrder = products.stream()
            .filter(isProductAvailableForBeingOrdered)
            .collect(Collectors.toMap(
                product -> product,
                product -> requestedProductsForOrder.get(product.getId())
            ));

        if (productsForNewOrder.size() != requestedProductsForOrder.size()) {
            throw ApplicationExceptionFactory.createCantFinishOrderException();
        }

        BigDecimal totalPrice = productsForNewOrder.entrySet().stream()
            .map(entry -> entry.getKey()
                .getPrice()
                .multiply(BigDecimal.valueOf(entry.getValue())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        productsForNewOrder.forEach(
            (product, takenQuantity) -> product.setQuantity(product.getQuantity() - takenQuantity)
        );

        if (!productsForNewOrder.entrySet().stream()
            .allMatch(entry -> entry.getKey().getQuantity() >= 0)) {
            throw ApplicationExceptionFactory.createCantFinishOrderException();
        }

        Order order = Order.builder()
            .account(account)
            .orderedProducts(productsForNewOrder.keySet())
            .totalPrice(totalPrice)
            .build();

        return save(order);
    }

    @Override
    public Page<Order> findAll(String login, Pageable pageable) {

        accountRepository.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);
        return orderRepository.findAll(pageable);
    }

    @Override
    public Order findOrderById(String login, Long id) {

        accountRepository.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

        return orderRepository.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createOrderNotFoundException);
    }

    private Order save(Order order) {
        try {
            orderRepository.saveAndFlush(order);
            return order;

        } catch (DataAccessException e) {
            var violationException = ExceptionUtil.findCause(e, ConstraintViolationException.class);

            if (nonNull(violationException) && nonNull(violationException.getConstraintName())) {
                return handleConstraintViolationException(violationException);
            }

            throw ApplicationExceptionFactory.createUnknownException();
        }
    }

    private Order handleConstraintViolationException(ConstraintViolationException e) {
        switch (requireNonNull(e.getConstraintName())) {
            default -> throw SystemExceptionFactory.createDbConstraintViolationException(e);
        }
    }
}
