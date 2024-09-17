package pl.lodz.p.edu.shop.logic.service.impl;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.edu.shop.dataaccess.model.entity.*;
import pl.lodz.p.edu.shop.dataaccess.repository.api.OrderRepository;
import pl.lodz.p.edu.shop.dataaccess.repository.api.ProductRepository;
import pl.lodz.p.edu.shop.dataaccess.repository.api.RateRepository;
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
    private final RateRepository rateRepository;

    public OrderServiceImpl(
        OrderRepository orderRepository,
        ProductRepository productRepository,
        ReadOnlyAccountRepository accountRepository,
        RateRepository rateRepository
    ) {
        requireNonNull(orderRepository, "OrderService requires non null orderRepository");
        requireNonNull(productRepository, "OrderService requires non null productRepository");
        requireNonNull(accountRepository, "OrderService requires non null accountRepository");
        requireNonNull(rateRepository, "OrderService required non null rateRepository");

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.accountRepository = accountRepository;
        this.rateRepository = rateRepository;
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
            .totalPrice(totalPrice)
            .build();

        Set<OrderedProduct> orderedProducts = productsForNewOrder.entrySet().stream()
            .map(productQuantityEntry -> {
                Product product = productQuantityEntry.getKey();
                Integer takenQuantity = productQuantityEntry.getValue();

                return OrderedProduct.builder()
                    .name(product.getName())
                    .quantity(takenQuantity)
                    .price(product.getPrice())
                    .account(account)
                    .product(product)
                    .order(order)
                    .build();
            }).collect(Collectors.toUnmodifiableSet());

        order.setOrderedProducts(orderedProducts);
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

        return orderRepository.findByIdWithOrderedProducts(id)
            .orElseThrow(ApplicationExceptionFactory::createOrderNotFoundException);
    }

    @Override
    public Rate rateOrderedProduct(String login, Long productId, Integer rateValue) {
        Account account = accountRepository.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

        OrderedProduct orderedProduct = productRepository.findOrderedProductByProductId(productId)
            .orElseThrow(ApplicationExceptionFactory::createProductNotFoundException);
        Product product = orderedProduct.getProduct();

        if (!isProductOrderedByUserWithGivenAccount(product, account)) {
            throw ApplicationExceptionFactory.createProductNotFoundException();
        }

        if (isProductAlreadyRatedByUserWithGivenAccount(product, account)) {
            throw ApplicationExceptionFactory.createProductAlreadyRatedException();
        }

        Rate rate = Rate.builder()
            .value(rateValue)
            .account(account)
            .product(product)
            .build();

        try {
            product.getRates().add(rate);
            orderedProduct.setRate(rate);
            rateRepository.save(rate);
            Double newAverageRating = rateRepository.findAverageRatingForProduct(productId);
            product.setAverageRating(newAverageRating);
            return rate;

        } catch (DataAccessException e) {
            return handleDataAccessException(e);
        }
    }

    @Override
    public Rate reRateOrderedProduct(String login, Long productId, Integer rateValue) {
        Account account = accountRepository.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

        Product product = productRepository.findById(productId)
            .orElseThrow(ApplicationExceptionFactory::createProductNotFoundException);

        Rate userRate = product.getRates().stream()
            .filter(rate -> rate.getAccount().equals(account))
            .findFirst()
            .orElseThrow(ApplicationExceptionFactory::createRateNotFoundException);

        try {
            userRate.setValue(rateValue);
            rateRepository.save(userRate);
            Double newAverageRating = rateRepository.findAverageRatingForProduct(productId);
            product.setAverageRating(newAverageRating);
            return userRate;

        } catch (DataAccessException e) {
            return handleDataAccessException(e);
        }
    }

    @Override
    public void removeRate(String login, Long productId) {

        Account account = accountRepository.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);
        OrderedProduct orderedProduct = productRepository.findOrderedProductByProductId(productId)
            .orElseThrow(ApplicationExceptionFactory::createProductNotFoundException);
        Product product = orderedProduct.getProduct();

        Rate clientRate = product.getRates().stream()
            .filter(rate -> rate.getAccount().equals(account))
            .findFirst()
            .orElseThrow(ApplicationExceptionFactory::createRateNotFoundException);

        try {
            rateRepository.delete(clientRate);
            product.getRates().remove(clientRate);
            orderedProduct.setRate(null);
            Double newAverageRating = rateRepository.findAverageRatingForProduct(productId);
            product.setAverageRating(newAverageRating);

        } catch (DataAccessException e) {
            handleDataAccessException(e);
        }
    }

    private boolean isProductOrderedByUserWithGivenAccount(Product product, Account userAccount) {
        return orderRepository.findAllByAccountId(userAccount.getId()).stream()
            .flatMap(order -> order.getOrderedProducts().stream())
            .anyMatch(orderedProduct -> orderedProduct.getProduct().equals(product));
    }

    private boolean isProductAlreadyRatedByUserWithGivenAccount(Product product, Account userAccount) {
        return product.getRates().stream()
            .anyMatch(rate -> rate.getAccount().equals(userAccount));
    }

    private Order save(Order order) {
        try {
            orderRepository.saveAndFlush(order);
            return order;

        } catch (DataAccessException e) {
            return handleDataAccessException(e);
        }
    }

    private <T> T handleDataAccessException(DataAccessException e) {
        var violationException = ExceptionUtil.findCause(e, ConstraintViolationException.class);

        if (nonNull(violationException) && nonNull(violationException.getConstraintName())) {
            return handleConstraintViolationException(violationException);
        }

        throw ApplicationExceptionFactory.createUnknownException();
    }

    private <T> T handleConstraintViolationException(ConstraintViolationException e) {
        requireNonNull(e.getConstraintName());
        throw SystemExceptionFactory.createDbConstraintViolationException(e);
    }
}
