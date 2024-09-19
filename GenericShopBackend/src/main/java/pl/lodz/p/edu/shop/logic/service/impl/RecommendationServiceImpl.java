package pl.lodz.p.edu.shop.logic.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.edu.shop.dataaccess.dao.api.ProductDAO;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Product;
import pl.lodz.p.edu.shop.dataaccess.repository.api.OrderRepository;
import pl.lodz.p.edu.shop.dataaccess.repository.api.ProductRepository;
import pl.lodz.p.edu.shop.dataaccess.repository.api.ReadOnlyAccountRepository;
import pl.lodz.p.edu.shop.exception.ApplicationExceptionFactory;
import pl.lodz.p.edu.shop.logic.model.UserPreferences;
import pl.lodz.p.edu.shop.logic.service.api.RecommendationService;

import java.util.*;

import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;

@Slf4j

@Service
@Transactional(transactionManager = "ordersModTxManager", propagation = Propagation.REQUIRES_NEW)
@Qualifier("RecommendationServiceImpl")
class RecommendationServiceImpl implements RecommendationService {

    private final ReadOnlyAccountRepository accountRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductDAO productDAO;

    public RecommendationServiceImpl(
        ReadOnlyAccountRepository accountRepository,
        OrderRepository orderRepository,
        ProductRepository productRepository,
        ProductDAO productDAO
    ) {
        requireNonNull(accountRepository,"Recommendation service requires non null account repository");
        requireNonNull(orderRepository,"Recommendation service requires non null order repository");
        requireNonNull(productRepository,"Recommendation service requires non null product repository");
        requireNonNull(productDAO,"Recommendation service requires non null product DAO");

        this.accountRepository = accountRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productDAO = productDAO;
    }

    @Override
    public List<Product> findByRecommendation(String login, UserPreferences userPreferences) {

        Account account = accountRepository.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);
        List<Product> recommendedProducts = new ArrayList<>(10);

        // IF PRESENT ADD TO ALGORITHM DATA FROM FRONTEND WITH USER PREFERENCES
        Optional.ofNullable(userPreferences.categoryPreferences())
            .filter(not(Map::isEmpty))
            .ifPresent(categoryPref -> {
                List<String> namesOfMostCommonCategories = categoryPref.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .limit(5)
                    .map(Map.Entry::getKey)
                    .toList();

                List<Product> productRecommendationsFromCategoryPreferences = productRepository
                    .findProductsByCategories(namesOfMostCommonCategories);
                recommendedProducts.addAll(productRecommendationsFromCategoryPreferences);
            });

        Optional.ofNullable(userPreferences.productPreferences())
            .filter(not(Map::isEmpty))
            .ifPresent(productPref -> {
                List<Long> idsOfMostCommonProducts = productPref.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .limit(5)
                    .map(Map.Entry::getKey)
                    .toList();

                List<Product> productRecommendationsFromProductPreferences = productRepository
                    .findProductsByIds(idsOfMostCommonProducts);
                recommendedProducts.addAll(productRecommendationsFromProductPreferences);
            });


        return Collections.unmodifiableList(recommendedProducts);
    }
}
