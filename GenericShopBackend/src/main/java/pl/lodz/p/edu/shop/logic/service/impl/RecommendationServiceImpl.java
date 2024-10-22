package pl.lodz.p.edu.shop.logic.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.edu.shop.dataaccess.dao.api.ProductDAO;
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
        ProductRepository productRepository,
        ProductDAO productDAO
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
        Map<Long, Double> productPreferences = userPreferences.productPreferences();
        if (nonNull(productPreferences) && !productPreferences.isEmpty()) {

            // PUT ONE PRODUCT THAT IS MOST FREQUENTLY SEARCHED BY THE CLIENT
            productPreferences.entrySet().stream()
                .filter(entry -> recommendedProducts.stream().noneMatch(rp -> rp.getId().equals(entry.getKey())))
                .max(Map.Entry.comparingByValue())
                .flatMap(entry -> productRepository.findById(entry.getKey()))
                .ifPresent(recommendedProducts::add);
            
            Map<String, Double> categoryPreferences = userPreferences.categoryPreferences();
            boolean areCategoryPreferencesAvailable = nonNull(categoryPreferences) && !categoryPreferences.isEmpty();

            Comparator<Product> productInterestComparator = areCategoryPreferencesAvailable ?
                comparingDouble(product -> {
                    Double productPoints = productPreferences.get(product.getId());
                    Double categoryPoints = Optional.ofNullable(
                        categoryPreferences.get(product.getCategory().getName())
                    ).orElse(0.0);
                    return productPoints + categoryPoints;
                }) : comparingDouble(product -> productPreferences.get(product.getId()));
            
            // PUT SEARCHED PRODUCTS WITH BEST SUM OF INTEREST POINTS
            List<Product> productsWithBestInterestPoints =
                // need to query db for product data of each id
                productRepository.findProductsByIds(productPreferences.keySet()).stream()
                    .sorted(productInterestComparator)
                    .toList();
            recommendedProducts.addAll(productsWithBestInterestPoints);
            
            if (recommendedProducts.size() < numberOfRecords && areCategoryPreferencesAvailable) {
                Queue<String> categoriesSortedByInterestPoints = categoryPreferences.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
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
        }

        recommendedProducts.removeIf(not(Product::isAvailable));

//        // PUT OTHER PRODUCTS WHEN THERE IS NOT ENOUGH
//        if (recommendedProducts.size() < numberOfRecords) {
//            Supplier<Product> otherProductSupplier = () -> Product.builder().build();
//            while (recommendedProducts.size() < numberOfRecords) {
//                try {
//                    recommendedProducts.add(otherProductSupplier.get());
//                } catch (Exception e) {
//                    break;
//                }
//            }
//        }

        return recommendedProducts.stream()
            .limit(numberOfRecords)
            .toList();
    }
}
