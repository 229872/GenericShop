package pl.lodz.p.edu.shop.logic.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.edu.shop.dataaccess.model.entity.OrderedProduct;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Product;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Rate;
import pl.lodz.p.edu.shop.dataaccess.repository.api.CategoryRepository;
import pl.lodz.p.edu.shop.dataaccess.repository.api.ProductRepository;
import pl.lodz.p.edu.shop.dataaccess.repository.api.ReadOnlyAccountRepository;
import pl.lodz.p.edu.shop.exception.ApplicationExceptionFactory;
import pl.lodz.p.edu.shop.logic.model.UserPreferences;
import pl.lodz.p.edu.shop.logic.service.api.RecommendationService;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingDouble;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;

@Slf4j

@Service
@Transactional(transactionManager = "ordersModTxManager", propagation = Propagation.REQUIRES_NEW)
@Qualifier("RecommendationServiceImpl")
class RecommendationServiceImpl implements RecommendationService {

    private final ReadOnlyAccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public RecommendationServiceImpl(
        ReadOnlyAccountRepository accountRepository,
        CategoryRepository categoryRepository,
        ProductRepository productRepository
    ) {
        requireNonNull(accountRepository,"Recommendation service requires non null account repository");
        requireNonNull(categoryRepository,"Recommendation service requires non null category repository");
        requireNonNull(productRepository,"Recommendation service requires non null product repository");

        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> findByRecommendation(String login, UserPreferences userPreferences, Integer numberOfRecords) {

        accountRepository.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);
        Set<Product> recommendedProducts = new LinkedHashSet<>(numberOfRecords);

        // PUT ONE PRODUCT THAT IS MOST FREQUENTLY PURCHASED BY THE CLIENT
        productRepository.findTheMostFrequentlyPurchasedProducts(login).stream()
            .max(comparing(orderedProduct -> orderedProduct.getRate().orElse(Rate.builder().value(0).build())))
            .map(OrderedProduct::getProduct)
            .ifPresent(recommendedProducts::add);

        // PUT PRODUCTS FROM FRONTED PREFERENCES
        if (
            nonNull(userPreferences) &&
            nonNull(userPreferences.productPreferences()) &&
            !userPreferences.productPreferences().isEmpty()
        ) {
            Map<Long, Double> productPreferences = userPreferences.productPreferences();

            // PUT ONE PRODUCT THAT IS MOST FREQUENTLY SEARCHED BY THE CLIENT
            productPreferences.entrySet().stream()
                .filter(entry -> recommendedProducts.stream().noneMatch(rp -> rp.getId().equals(entry.getKey())))
                .max(Comparator.comparing(e -> Optional.ofNullable(e.getValue()).orElse(0.0)))
                .flatMap(entry -> productRepository.findById(entry.getKey()))
                .ifPresent(recommendedProducts::add);
            
            Map<String, Double> categoryPreferences = userPreferences.categoryPreferences();

            Comparator<Product> productInterestComparator = areCategoryPreferencesAvailable(categoryPreferences) ?
                comparingDouble(product -> {
                    Double productPoints = Optional.ofNullable(
                        productPreferences.get(product.getId())
                    ).orElse(0.0);
                    Double categoryPoints = Optional.ofNullable(
                        categoryPreferences.get(product.getCategory().getName())
                    ).orElse(0.0);
                    return productPoints + categoryPoints;
                }) : comparingDouble(product -> Optional.ofNullable(
                        productPreferences.get(product.getId())
                    ).orElse(0.0));
            
            // PUT SEARCHED PRODUCTS WITH BEST SUM OF INTEREST POINTS
            List<Product> productsWithBestInterestPoints =
                // need to query db for product data of each id
                productRepository.findProductsByIds(productPreferences.keySet()).stream()
                    .sorted(productInterestComparator.reversed())
                    .toList();
            recommendedProducts.addAll(productsWithBestInterestPoints);
        }
        
        if (
            nonNull(userPreferences) &&
            recommendedProducts.size() < numberOfRecords &&
            areCategoryPreferencesAvailable(userPreferences.categoryPreferences())
        ) {
            Map<String, Double> categoryPreferences = userPreferences.categoryPreferences();
            
            // PUT PRODUCTS WITH SEARCHED CATEGORIES
            Queue<String> categoriesSortedByInterestPoints = categoryPreferences.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(ArrayDeque::new));

            while (recommendedProducts.size() < numberOfRecords && !categoriesSortedByInterestPoints.isEmpty()) {
                Optional.ofNullable(categoriesSortedByInterestPoints.poll())
                    .flatMap(categoryRepository::findByName)
                    .ifPresent(
                        category -> recommendedProducts.addAll(productRepository.findByCategory(category))
                    );
            }
        }

        recommendedProducts.removeIf(not(Product::isAvailable));

        // PUT OTHER PRODUCTS WHEN THERE IS NOT ENOUGH
        if (recommendedProducts.size() < numberOfRecords) {
            Queue<Supplier<List<Product>>> queue = new ArrayDeque<>(List.of(
                productRepository::findBestRatedProducts,
                productRepository::findNewestProducts,
                productRepository::findCheapestProducts,
                productRepository::findProductsThatAreRunningOut,
                productRepository::findAvailableProducts
            ));

            while (recommendedProducts.size() < numberOfRecords) {
                try {
                    recommendedProducts.addAll(queue.remove().get());
                } catch (Exception e) {
                    break;
                }
            }
        }

        return recommendedProducts.stream()
            .limit(numberOfRecords)
            .toList();
    }
    
    private static boolean areCategoryPreferencesAvailable(Map<String, Double> categoryPreferences) {
        return nonNull(categoryPreferences) && !categoryPreferences.isEmpty();
    }
}
