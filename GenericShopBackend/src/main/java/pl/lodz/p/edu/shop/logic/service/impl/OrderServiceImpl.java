package pl.lodz.p.edu.shop.logic.service.impl;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
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
import pl.lodz.p.edu.shop.logic.model.ProductForOrder;
import pl.lodz.p.edu.shop.logic.service.api.OrderService;
import pl.lodz.p.edu.shop.util.ExceptionUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
    public Order placeAndOrder(String login, List<ProductForOrder> requestedProductsForOrder) {

        Account account = accountRepository.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);
        List<Product> products = productRepository.findAll();

        List<Long> productsId = requestedProductsForOrder.stream()
            .map(ProductForOrder::id)
            .toList();

        Predicate<Product> isProductAvailableForBeingOrdered = product -> {
            Integer quantity = product.getQuantity();
            return !product.isArchival() && productsId.contains(product.getId()) && quantity > 0;
        };
        Function<Product, Integer> bindProductWithQuantity = product -> {
            return requestedProductsForOrder.stream()
                .filter(productForOrder -> productForOrder.id().equals(product.getId()))
                .map(ProductForOrder::quantity)
                .findAny()
                .orElseThrow(ApplicationExceptionFactory::createCantFinishOrderException);
        };

        Map<Product, Integer> productsForNewOrder = products.stream()
            .filter(isProductAvailableForBeingOrdered)
            .collect(Collectors.toMap(
                product -> product,
                bindProductWithQuantity
            ));

        if (productsForNewOrder.size() != requestedProductsForOrder.size()) {
            throw ApplicationExceptionFactory.createCantFinishOrderException();
        }

        BigDecimal totalPrice = productsForNewOrder.entrySet().stream()
            .map(entry -> entry.getKey()
                .getPrice()
                .multiply(BigDecimal.valueOf(entry.getValue())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);


        Order order = Order.builder()
            .account(account)
            .orderedProducts(productsForNewOrder.keySet())
            .totalPrice(totalPrice)
            .build();

        return save(order);
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
