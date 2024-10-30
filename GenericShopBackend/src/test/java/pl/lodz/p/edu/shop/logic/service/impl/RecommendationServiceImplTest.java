package pl.lodz.p.edu.shop.logic.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.lodz.p.edu.shop.OrdersModuleTestData;
import pl.lodz.p.edu.shop.dataaccess.model.entity.*;
import pl.lodz.p.edu.shop.dataaccess.repository.api.CategoryRepository;
import pl.lodz.p.edu.shop.dataaccess.repository.api.ProductRepository;
import pl.lodz.p.edu.shop.dataaccess.repository.api.ReadOnlyAccountRepository;
import pl.lodz.p.edu.shop.logic.model.UserPreferences;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@DisplayName("Unit tests for RecommendationServiceImpl")
@ExtendWith(MockitoExtension.class)
class RecommendationServiceImplTest {

    @Mock
    private ReadOnlyAccountRepository accountRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private RecommendationServiceImpl underTest;

    @AfterEach
    void tearDown() {
        OrdersModuleTestData.resetCounter();
    }

    @Test
    @DisplayName("""
        Algorithm should return set of products with one that user most frequently purchase. 
        If there is only one ordered product, it should return set with given number of records with this item. 
        Since user preferences and other criteria are not available, and therefore the number of records found is not 
        sufficient, algorithm should supplement the result with regular products available for purchase.
        """)
    void findByRecommendation_most_frequently_purchased_product_test1() {
        //given
        Account givenAccount = Account.builder().login("testLogin").build();

        Product givenProduct1 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct2 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct3 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct4 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct5 = OrdersModuleTestData.buildDefaultProduct();

        OrderedProduct orderedProduct = OrderedProduct.builder()
            .product(givenProduct1)
            .build();

        given(accountRepository.findByLogin(givenAccount.getLogin()))
            .willReturn(Optional.of(givenAccount));

        given(productRepository.findTheMostFrequentlyPurchasedProducts(givenAccount.getLogin()))
            .willReturn(List.of(orderedProduct));

        given(productRepository.findAvailableProducts())
            .willReturn(List.of(givenProduct2, givenProduct3, givenProduct4, givenProduct5));

        //when
        List<Product> result = underTest.findByRecommendation(givenAccount.getLogin(), null, 3);

        //then
        assertThat(result)
            .contains(givenProduct1)
            .hasSize(3);
    }

    @Test
    @DisplayName("""
        Algorithm should return set of products with one that user most frequently purchase. 
        If there are many most frequently purchased products, the one found that received the highest rating from the customer is searched for.
        If there are few of them, the random one is picked. 
        Since user preferences and other criteria are not available, and therefore the number of records found is not 
        sufficient, algorithm should supplement the result with regular products available for purchase.
        """)
    void findByRecommendation_most_frequently_purchased_product_test2() {
        //given
        Account givenAccount = Account.builder().login("testLogin").build();

        Product givenProduct1 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct2 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct3 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct4 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct5 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct6 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct7 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct8 = OrdersModuleTestData.buildDefaultProduct();

        OrderedProduct orderedProductWithNotBestRate = OrderedProduct.builder()
            .product(givenProduct1)
            .rate(Rate.builder().value(2).build())
            .build();

        OrderedProduct orderedProductWithoutRate = OrderedProduct.builder()
            .product(givenProduct2)
            .build();

        OrderedProduct orderedProductWithBestRate = OrderedProduct.builder()
            .product(givenProduct3)
            .rate(Rate.builder().value(4).build())
            .build();

        OrderedProduct orderedProductWithBestRate2 = OrderedProduct.builder()
            .product(givenProduct4)
            .rate(Rate.builder().value(4).build())
            .build();

        given(accountRepository.findByLogin(givenAccount.getLogin()))
            .willReturn(Optional.of(givenAccount));

        given(productRepository.findTheMostFrequentlyPurchasedProducts(givenAccount.getLogin()))
            .willReturn(List.of(orderedProductWithNotBestRate, orderedProductWithoutRate, orderedProductWithBestRate, orderedProductWithBestRate2));

        given(productRepository.findAvailableProducts())
            .willReturn(List.of(givenProduct5, givenProduct6, givenProduct7, givenProduct8));

        //when
        List<Product> result = underTest.findByRecommendation(givenAccount.getLogin(), null, 3);

        //then
        assertThat(result)
            .containsAnyOf(orderedProductWithBestRate.getProduct(), orderedProductWithBestRate2.getProduct())
            .doesNotContain(orderedProductWithNotBestRate.getProduct(), orderedProductWithoutRate.getProduct())
            .hasSize(3);
    }

    @Test
    @DisplayName("""
        Algorithm should return set of products with one that user most frequently purchase. 
        If client has not purchased any product yet, the algorithm should not add any product in this step. 
        Since user preferences and other criteria are not available, and therefore the number of records found is not 
        sufficient, algorithm should supplement the result with regular products available for purchase.
        """)
    void findByRecommendation_most_frequently_purchased_product_test3() {
        //given
        Account givenAccount = Account.builder().login("testLogin").build();

        Product givenProduct1 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct2 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct3 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct4 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct5 = OrdersModuleTestData.buildDefaultProduct();
        List<Product> givenProducts = List.of(givenProduct1, givenProduct2, givenProduct3, givenProduct4, givenProduct5);

        given(accountRepository.findByLogin(givenAccount.getLogin()))
            .willReturn(Optional.of(givenAccount));

        given(productRepository.findTheMostFrequentlyPurchasedProducts(givenAccount.getLogin()))
            .willReturn(List.of());

        given(productRepository.findAvailableProducts())
            .willReturn(givenProducts);

        //when
        List<Product> result = underTest.findByRecommendation(givenAccount.getLogin(), null, 3);

        //then
        assertThat(result)
            .allMatch(givenProducts::contains)
            .hasSize(3);
    }

    @Test
    @DisplayName("""
        If not empty user preferences are given, algorithm should return set of products with one that user 
        most frequently searched for. It then takes the less searched products and calculates a desirability level for 
        each based on the points collected by product and category. Category points should be omitted when are not set.   
        Because most frequently purchased product is not available step one is omitted.
        Because algorithm finds the requested number of records, it should not supplement results using other criteria.
        """)
    void findByRecommendation_user_preferences_test1() {
        //given
        Account givenAccount = Account.builder().login("testLogin").build();

        Product givenProduct1 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct2 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct3 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct4 = OrdersModuleTestData.buildDefaultProduct();
        Long givenIdOfMostSearchedProduct = givenProduct4.getId();

        Map<Long, Double> productPreferences = Map.ofEntries(
            entry(givenProduct3.getId(), 33.0),
            entry(givenProduct1.getId(), 24.0),
            entry(givenIdOfMostSearchedProduct, 41.5),
            entry(givenProduct2.getId(), 12.0)
        );
        UserPreferences preferences = new UserPreferences(null, productPreferences);

        Map<Long, Product> productMap = Map.ofEntries(
            entry(givenProduct1.getId(), givenProduct1),
            entry(givenProduct2.getId(), givenProduct2),
            entry(givenProduct3.getId(), givenProduct3),
            entry(givenIdOfMostSearchedProduct, givenProduct4)
        );

        given(accountRepository.findByLogin(givenAccount.getLogin()))
            .willReturn(Optional.of(givenAccount));

        given(productRepository.findTheMostFrequentlyPurchasedProducts(givenAccount.getLogin()))
            .willReturn(List.of());

        given(productRepository.findById(anyLong()))
            .willAnswer(invocationOnMock -> {
                Long id = invocationOnMock.getArgument(0);
                return Optional.ofNullable(productMap.get(id));
            });

        given(productRepository.findProductsByIds(anySet()))
            .willReturn(productMap.values().stream().toList());

        //when
        List<Product> result = underTest.findByRecommendation(givenAccount.getLogin(), preferences, 3);

        //then
        assertThat(result)
            .containsExactly(
                productMap.get(givenIdOfMostSearchedProduct),
                productMap.get(givenProduct3.getId()),
                productMap.get(givenProduct1.getId())
            )
            .hasSize(3);
    }

    @Test
    @DisplayName("""
        If not empty user preferences are given, algorithm should return set of products with one that user 
        most frequently searched for. It then takes the less searched products and calculates a desirability level for 
        each based on the points collected by product and category. 
        If points are not provided, them should be treated as 0. 
        Category points should be omitted when are not set.   
        Because most frequently purchased product is not available step one is omitted.
        Since requested number of records are not available, it should supplement results with regular products available for purchase.
        """)
    void findByRecommendation_user_preferences_test2() {
        //given
        Account givenAccount = Account.builder().login("testLogin").build();

        Product givenProduct1 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct2 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct3 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct4 = OrdersModuleTestData.buildDefaultProduct();
        Long givenIdOfProductWithNullPoints = 1L;
        Long givenIdOfMostSearchedProduct = 2L;
        List<Product> givenProducts = List.of(givenProduct3, givenProduct4);

        Map<Long, Double> productPreferences = Collections.unmodifiableMap(new HashMap<>() {{
           put(givenIdOfProductWithNullPoints, null);
           put(givenIdOfMostSearchedProduct, 20.0);
        }});
        UserPreferences preferences = new UserPreferences(null, productPreferences);

        Map<Long, Product> productMap = Map.ofEntries(
            entry(givenIdOfProductWithNullPoints, givenProduct1),
            entry(givenIdOfMostSearchedProduct, givenProduct2)
        );

        given(accountRepository.findByLogin(givenAccount.getLogin()))
            .willReturn(Optional.of(givenAccount));

        given(productRepository.findTheMostFrequentlyPurchasedProducts(givenAccount.getLogin()))
            .willReturn(List.of());

        given(productRepository.findById(anyLong()))
            .willAnswer(invocationOnMock -> {
                Long id = invocationOnMock.getArgument(0);
                return Optional.ofNullable(productMap.get(id));
            });

        given(productRepository.findProductsByIds(anySet()))
            .willReturn(productMap.values().stream().toList());

        given(productRepository.findAvailableProducts())
            .willReturn(givenProducts);

        //when
        List<Product> result = underTest.findByRecommendation(givenAccount.getLogin(), preferences, 3);

        //then
        assertThat(result)
            .containsExactly(
                productMap.get(givenIdOfMostSearchedProduct),
                productMap.get(givenIdOfProductWithNullPoints),
                givenProduct3
            )
            .doesNotContain(givenProduct4)
            .hasSize(3);
    }

    @Test
    @DisplayName("""
        If not empty user preferences are given, algorithm should return set of products with one that user 
        most frequently searched for. It then takes the less searched products and calculates a desirability level for 
        each based on the points collected by product and category.
        If product with given id cannot be found, it should be omitted. 
        Category points should be omitted when are not set.   
        Because most frequently purchased product is not available step one is omitted.
        Since requested number of records are not available, it should supplement results with regular products available for purchase.
        """)
    void findByRecommendation_user_preferences_test3() {
        //given
        Account givenAccount = Account.builder().login("testLogin").build();

        Product givenProduct1 = OrdersModuleTestData.buildDefaultProduct();
        givenProduct1.setArchival(true);
        Product givenProduct2 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct3 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct4 = OrdersModuleTestData.buildDefaultProduct();
        Product givenProduct5 = OrdersModuleTestData.buildDefaultProduct();
        Long givenIdOfProductWhichIsNotAvailable = 1L;
        Long givenIdOfLessSearchedProduct = 2L;
        Long givenIdOfProductThatDoesntExist = 6L;
        List<Product> givenProducts = List.of(givenProduct3, givenProduct4, givenProduct5);

        Map<Long, Double> productPreferences = Collections.unmodifiableMap(new HashMap<>() {{
            put(givenIdOfProductWhichIsNotAvailable, 22.0);
            put(givenIdOfLessSearchedProduct, 20.0);
            put(null, 21.0);
            put(givenIdOfProductThatDoesntExist, 23.0);
        }});
        UserPreferences preferences = new UserPreferences(null, productPreferences);

        Map<Long, Product> productMap = Collections.unmodifiableMap(new HashMap<>() {{
            put(givenIdOfProductWhichIsNotAvailable, givenProduct1);
            put(givenIdOfLessSearchedProduct, givenProduct2);
        }});

        given(accountRepository.findByLogin(givenAccount.getLogin()))
            .willReturn(Optional.of(givenAccount));

        given(productRepository.findTheMostFrequentlyPurchasedProducts(givenAccount.getLogin()))
            .willReturn(List.of());

        given(productRepository.findById(anyLong()))
            .willAnswer(invocationOnMock -> {
                Long id = invocationOnMock.getArgument(0);
                return Optional.ofNullable(productMap.get(id));
            });

        given(productRepository.findProductsByIds(anySet()))
            .willReturn(productMap.values().stream().toList());

        given(productRepository.findAvailableProducts())
            .willReturn(givenProducts);

        //when
        List<Product> result = underTest.findByRecommendation(givenAccount.getLogin(), preferences, 3);

        //then
        assertThat(result)
            .containsExactly(
                productMap.get(givenIdOfLessSearchedProduct),
                givenProduct3,
                givenProduct4
            )
            .doesNotContain(productMap.get(givenIdOfProductThatDoesntExist))
            .doesNotContain(productMap.get(givenIdOfProductWhichIsNotAvailable))
            .hasSize(3);
    }

    @Test
    @DisplayName("""
        If not empty user preferences are given, algorithm should return set of products with one that user
        most frequently searched for. It then takes the less searched products and calculates a desirability level for
        each based on the points collected by product and category. Category points should be included.
        Because most frequently purchased product is not available step one is omitted.
        Products coming solely from category preferences are not included in this test.
        Since requested number of records are not available, it should supplement results with regular products available for purchase.
        """)
    void findByRecommendation_user_preferences_test4() {
        //given
        Account givenAccount = Account.builder().login("testLogin").build();
        Category givenCategory1 = Category.builder().name("test1").build();
        Category givenCategory2 = Category.builder().name("test2").build();
        Category givenCategory3 = Category.builder().name("test3").build();
        Product givenProductWithCategory1 = OrdersModuleTestData.getDefaultProductBuilder()
            .category(givenCategory1)
            .build();
        Product givenProduct2WithCategory1 = OrdersModuleTestData.getDefaultProductBuilder()
            .category(givenCategory1)
            .build();
        Product givenProductWithCategory2 = OrdersModuleTestData.getDefaultProductBuilder()
            .category(givenCategory2)
            .build();
        Product givenProductWithCategory3 = OrdersModuleTestData.getDefaultProductBuilder()
            .category(givenCategory3)
            .build();
        Product givenProductWithoutCategory = OrdersModuleTestData.buildDefaultProduct();
        Product givenProductWithoutCategoryWithMaxPoints = OrdersModuleTestData.buildDefaultProduct();
        Product givenNonArchivalProduct1 = OrdersModuleTestData.buildDefaultProduct();
        Product givenNonArchivalProduct2 = OrdersModuleTestData.buildDefaultProduct();

        List<Product> nonArchivalProducts = List.of(givenNonArchivalProduct1, givenNonArchivalProduct2);

        Map<Long, Double> productPreferences = Map.ofEntries(
            entry(givenProductWithCategory1.getId(), 40.0),                 // 40.0 + 25.0 =   65.0 3)
            entry(givenProduct2WithCategory1.getId(), 30.0),                // 30.0 + 25.0 =   55.0 5)
            entry(givenProductWithCategory2.getId(), 10.0),                 // 10.0 + 80.5 =   90.5 2)
            entry(givenProductWithCategory3.getId(), 15.0),                 // 15.0 + 30.0 =   45.0 6)
            entry(givenProductWithoutCategory.getId(), 60.0),               // no category     60.0 4)
            entry(givenProductWithoutCategoryWithMaxPoints.getId(), 85.0)   // MAX in products 85.0 1)
        );
        Map<String, Double> categoryPreferences = Map.ofEntries(
            entry(givenCategory1.getName(), 25.0),
            entry(givenCategory2.getName(), 80.5),
            entry(givenCategory3.getName(), 30.0)
        );
        UserPreferences preferences = new UserPreferences(categoryPreferences, productPreferences);

        Map<Long, Product> productMap = Map.ofEntries(
            entry(givenProductWithCategory1.getId(), givenProductWithCategory1),
            entry(givenProduct2WithCategory1.getId(), givenProduct2WithCategory1),
            entry(givenProductWithCategory2.getId(), givenProductWithCategory2),
            entry(givenProductWithCategory3.getId(), givenProductWithCategory3),
            entry(givenProductWithoutCategory.getId(), givenProductWithoutCategory),
            entry(givenProductWithoutCategoryWithMaxPoints.getId(), givenProductWithoutCategoryWithMaxPoints)
        );

        given(accountRepository.findByLogin(givenAccount.getLogin()))
            .willReturn(Optional.of(givenAccount));

        given(productRepository.findTheMostFrequentlyPurchasedProducts(givenAccount.getLogin()))
            .willReturn(List.of());

        given(productRepository.findById(anyLong()))
            .willAnswer(invocationOnMock -> {
                Long id = invocationOnMock.getArgument(0);
                return Optional.ofNullable(productMap.get(id));
            });

        given(productRepository.findProductsByIds(anySet()))
            .willReturn(productMap.values().stream().toList());

        given(productRepository.findAvailableProducts())
            .willReturn(nonArchivalProducts);

        //when
        List<Product> result = underTest.findByRecommendation(givenAccount.getLogin(), preferences, 8);

        //then
        assertThat(result)
            .containsExactly(
                givenProductWithoutCategoryWithMaxPoints, // sum = 85.0 most frequently searched for
                givenProductWithCategory2,                // sum = 90.5 top 1 sum
                givenProductWithCategory1,                // sum = 65.0
                givenProductWithoutCategory,              // sum = 60.0
                givenProduct2WithCategory1,               // sum = 55.0
                givenProductWithCategory3,                // sum = 45.0
                givenNonArchivalProduct1,                 // product supplied
                givenNonArchivalProduct2                  // product supplied
            )
            .hasSize(8);
    }

    @Test
    @DisplayName("""
        If not empty user preferences are given, algorithm should return set of products with one that user
        most frequently searched for. It then takes the less searched products and calculates a desirability level for
        each based on the points collected by product and category. Category points should be included.
        Products coming solely from category preferences are included to complete the result.
        Because most frequently purchased product is not available step one is omitted.
        Since requested number of records are not available, it should supplement results with regular products available for purchase.
        """)
    void findByRecommendation_user_preferences_test5() {
        //given
        Account givenAccount = Account.builder().login("testLogin").build();
        Category givenCategory1 = Category.builder().id(1L).name("test1").build();
        Category givenCategory2 = Category.builder().id(2L).name("test2").build();
        Product givenProductWithCategory1 = OrdersModuleTestData.getDefaultProductBuilder()
            .category(givenCategory1)
            .build();
        Product givenProductWithCategory2 = OrdersModuleTestData.getDefaultProductBuilder()
            .category(givenCategory2)
            .build();
        Product givenProductWithoutCategory = OrdersModuleTestData.buildDefaultProduct();
        Product givenProductWithoutCategoryWithMaxPoints = OrdersModuleTestData.buildDefaultProduct();
        Product givenProductNotIncludedInProductPreferencesWithCategory1 = OrdersModuleTestData.getDefaultProductBuilder()
            .category(givenCategory1)
            .build();
        Product givenProductNotIncludedInProductPreferencesWithCategory2 = OrdersModuleTestData.getDefaultProductBuilder()
            .category(givenCategory2)
            .build();
        Product givenProduct2NotIncludedInProductPreferencesWithCategory2 = OrdersModuleTestData.getDefaultProductBuilder()
            .category(givenCategory2)
            .build();

        Product givenNonArchivalProduct1 = OrdersModuleTestData.buildDefaultProduct();
        Product givenNonArchivalProduct2 = OrdersModuleTestData.buildDefaultProduct();

        List<Product> nonArchivalProducts = List.of(givenNonArchivalProduct1, givenNonArchivalProduct2);

        Map<Long, Double> productPreferences = Map.ofEntries(
            entry(givenProductWithCategory1.getId(), 40.0),                 // 40.0 + 25.0 =   65.0 3)
            entry(givenProductWithCategory2.getId(), 10.0),                 // 10.0 + 80.5 =   90.5 2)
            entry(givenProductWithoutCategory.getId(), 60.0),               // no category     60.0 4)
            entry(givenProductWithoutCategoryWithMaxPoints.getId(), 85.0)   // MAX in products 85.0 1)
        );
        Map<String, Double> categoryPreferences = Map.ofEntries(
            entry(givenCategory1.getName(), 25.0),
            entry(givenCategory2.getName(), 80.5)
        );
        UserPreferences preferences = new UserPreferences(categoryPreferences, productPreferences);

        Map<Long, Product> productMap = Map.ofEntries(
            entry(givenProductWithCategory1.getId(), givenProductWithCategory1),
            entry(givenProductWithCategory2.getId(), givenProductWithCategory2),
            entry(givenProductWithoutCategory.getId(), givenProductWithoutCategory),
            entry(givenProductWithoutCategoryWithMaxPoints.getId(), givenProductWithoutCategoryWithMaxPoints)
        );

        Map<String, Category> categoryMap = Map.ofEntries(
            entry(givenCategory1.getName(), givenCategory1),
            entry(givenCategory2.getName(), givenCategory2)
        );

        Map<Category, List<Product>> categoryProductsMap = Map.ofEntries(
            entry(givenCategory1, List.of(givenProductNotIncludedInProductPreferencesWithCategory1)),
            entry(givenCategory2, List.of(
                givenProductNotIncludedInProductPreferencesWithCategory2,
                givenProduct2NotIncludedInProductPreferencesWithCategory2
            ))
        );

        given(accountRepository.findByLogin(givenAccount.getLogin()))
            .willReturn(Optional.of(givenAccount));

        given(productRepository.findTheMostFrequentlyPurchasedProducts(givenAccount.getLogin()))
            .willReturn(List.of());

        given(productRepository.findById(anyLong()))
            .willAnswer(invocationOnMock -> {
                Long id = invocationOnMock.getArgument(0);
                return Optional.ofNullable(productMap.get(id));
            });

        given(productRepository.findProductsByIds(anySet()))
            .willReturn(productMap.values().stream().toList());

        given(categoryRepository.findByName(anyString()))
            .willAnswer(invocationOnMock -> {
                String categoryName = invocationOnMock.getArgument(0);
                return Optional.ofNullable(categoryMap.get(categoryName));
            });

        given(productRepository.findByCategory(any(Category.class)))
            .willAnswer(invocationOnMock -> {
                Category category = invocationOnMock.getArgument(0);
                return categoryProductsMap.get(category);
            });

        given(productRepository.findAvailableProducts())
            .willReturn(nonArchivalProducts);

        //when
        List<Product> result = underTest.findByRecommendation(givenAccount.getLogin(), preferences, 8);

        //then
        assertThat(result)
            .containsExactly(
                givenProductWithoutCategoryWithMaxPoints,                   // sum = 85.0 most frequently searched for
                givenProductWithCategory2,                                  // sum = 90.5 top 1 sum
                givenProductWithCategory1,                                  // sum = 65.0
                givenProductWithoutCategory,                                // sum = 60.0
                givenProductNotIncludedInProductPreferencesWithCategory2,   // supplied product with most searched category = 80
                givenProduct2NotIncludedInProductPreferencesWithCategory2,  // supplied product with most searched category = 80
                givenProductNotIncludedInProductPreferencesWithCategory1,   // supplied product with searched category = 25
                givenNonArchivalProduct1                                    // product supplied
            )
            .hasSize(8);
    }

    @Test
    @DisplayName("""
        Should return result coming solely from category preferences. Products should be ordered by category points. 
        All other result sources are not included in this test.
        """)
    void findByRecommendation_category_preferences_test1() {
        //given
        Account givenAccount = Account.builder().login("testLogin").build();
        Category givenCategory1 = OrdersModuleTestData.buildDefaultCategory();
        Category givenCategory2 = OrdersModuleTestData.buildDefaultCategory();
        Category givenCategory3 = OrdersModuleTestData.buildDefaultCategory();
        Product givenProduct1WithCategory1 = OrdersModuleTestData.getDefaultProductBuilder()
            .category(givenCategory1)
            .build();
        Product givenProduct2WithCategory1 = OrdersModuleTestData.getDefaultProductBuilder()
            .category(givenCategory1)
            .build();
        Product givenProductWithCategory2 = OrdersModuleTestData.getDefaultProductBuilder()
            .category(givenCategory2)
            .build();
        Product givenProduct1WithCategory3 = OrdersModuleTestData.getDefaultProductBuilder()
            .category(givenCategory3)
            .build();
        Product givenProduct2WithCategory3 = OrdersModuleTestData.getDefaultProductBuilder()
            .category(givenCategory3)
            .build();
        Product givenProduct3WithCategory3 = OrdersModuleTestData.getDefaultProductBuilder()
            .category(givenCategory3)
            .build();

        Map<String, Double> categoryPreferences = Map.ofEntries(
            entry(givenCategory1.getName(), 80.0),
            entry(givenCategory2.getName(), 40.5),
            entry(givenCategory3.getName(), 90.0)
        );

        UserPreferences preferences = new UserPreferences(categoryPreferences, null);

        Map<String, Category> categoryMap = Map.ofEntries(
            entry(givenCategory1.getName(), givenCategory1),
            entry(givenCategory2.getName(), givenCategory2),
            entry(givenCategory3.getName(), givenCategory3)
        );

        Map<Category, List<Product>> categoryProductsMap = Map.ofEntries(
            entry(givenCategory1, List.of(givenProduct1WithCategory1, givenProduct2WithCategory3)),
            entry(givenCategory2, List.of(givenProductWithCategory2)),
            entry(givenCategory3, List.of(
                givenProduct1WithCategory3,
                givenProduct2WithCategory3,
                givenProduct3WithCategory3
            ))
        );

        given(accountRepository.findByLogin(givenAccount.getLogin()))
            .willReturn(Optional.of(givenAccount));

        given(categoryRepository.findByName(anyString()))
            .willAnswer(invocationOnMock -> {
                String categoryName = invocationOnMock.getArgument(0);
                return Optional.ofNullable(categoryMap.get(categoryName));
            });

        given(productRepository.findByCategory(any(Category.class)))
            .willAnswer(invocationOnMock -> {
                Category category = invocationOnMock.getArgument(0);
                return categoryProductsMap.get(category);
            });

        //when
        List<Product> result = underTest.findByRecommendation(givenAccount.getLogin(), preferences, 4);

        //then
        assertThat(result)
            .containsExactly(
                givenProduct1WithCategory3,  // product of category with best points
                givenProduct2WithCategory3,  // product of category with best points
                givenProduct3WithCategory3,  // product of category with best points
                givenProduct1WithCategory1   // product of category with second best points
            )
            .doesNotContain(
                givenProduct2WithCategory1, // product should be omitted due to number of records
                givenProductWithCategory2)
            .hasSize(4);
    }

    @Test
    @DisplayName("""
        Should return result coming solely from product supplier. Test check if order of supplied products is correct. 
        All other result sources are not included in this test.
        """)
    void findByRecommendation_product_supplier_test1() {
        //given
        Account givenAccount = Account.builder().login("testLogin").build();
        List<Product> givenCheapestProducts = List.of(
            OrdersModuleTestData.buildDefaultProduct(),
            OrdersModuleTestData.buildDefaultProduct()
        );
        List<Product> givenNewestProducts = List.of(
            OrdersModuleTestData.buildDefaultProduct(),
            OrdersModuleTestData.buildDefaultProduct()
        );
        List<Product> givenBestRatedProducts = List.of(
            OrdersModuleTestData.buildDefaultProduct(),
            OrdersModuleTestData.buildDefaultProduct()
        );
        List<Product> givenAvailableProducts = List.of(
            OrdersModuleTestData.buildDefaultProduct(),
            OrdersModuleTestData.buildDefaultProduct()
        );
        List<Product> givenProductsThatAreRunningOut = List.of(
            OrdersModuleTestData.buildDefaultProduct(),
            OrdersModuleTestData.buildDefaultProduct()
        );

        given(accountRepository.findByLogin(givenAccount.getLogin()))
            .willReturn(Optional.of(givenAccount));

        given(productRepository.findBestRatedProducts())
            .willReturn(givenBestRatedProducts);

        given(productRepository.findNewestProducts())
            .willReturn(givenNewestProducts);

        given(productRepository.findCheapestProducts())
            .willReturn(givenCheapestProducts);

        given(productRepository.findProductsThatAreRunningOut())
            .willReturn(givenProductsThatAreRunningOut);

        given(productRepository.findAvailableProducts())
            .willReturn(givenAvailableProducts);

        //when
        List<Product> result = underTest.findByRecommendation(givenAccount.getLogin(), null, 10);

        //then
        Product[] correctOrderProducts = Stream.of(
                givenBestRatedProducts.stream(),
                givenNewestProducts.stream(),
                givenCheapestProducts.stream(),
                givenProductsThatAreRunningOut.stream(),
                givenAvailableProducts.stream()
            )
            .flatMap(stream -> stream)
            .toArray(Product[]::new);
        assertThat(result)
            .containsExactly(correctOrderProducts)
            .hasSize(10);
    }

    @Test
    @DisplayName("""
        Should return result coming solely from product supplier. Test check if result will be reduced to given number of records. 
        All other result sources are not included in this test.
        """)
    void findByRecommendation_product_supplier_test2() {
        //given
        Account givenAccount = Account.builder().login("testLogin").build();
        List<Product> givenCheapestProducts = List.of(
            OrdersModuleTestData.buildDefaultProduct(),
            OrdersModuleTestData.buildDefaultProduct(),
            OrdersModuleTestData.buildDefaultProduct(),
            OrdersModuleTestData.buildDefaultProduct()
        );
        List<Product> givenNewestProducts = List.of(
            OrdersModuleTestData.buildDefaultProduct(),
            OrdersModuleTestData.buildDefaultProduct()
        );
        List<Product> givenBestRatedProducts = List.of(
            OrdersModuleTestData.buildDefaultProduct(),
            OrdersModuleTestData.buildDefaultProduct()
        );

        given(accountRepository.findByLogin(givenAccount.getLogin()))
            .willReturn(Optional.of(givenAccount));

        given(productRepository.findBestRatedProducts())
            .willReturn(givenBestRatedProducts);

        given(productRepository.findNewestProducts())
            .willReturn(givenNewestProducts);

        given(productRepository.findCheapestProducts())
            .willReturn(givenCheapestProducts);


        //when
        List<Product> result = underTest.findByRecommendation(givenAccount.getLogin(), null, 5);

        //then
        Product[] correctOrderProducts = Stream.of(
                givenBestRatedProducts,
                givenNewestProducts,
                List.of(givenCheapestProducts.get(0))
            )
            .flatMap(List::stream)
            .toArray(Product[]::new);
        assertThat(result)
            .containsExactly(correctOrderProducts)
            .hasSize(5);
    }

    @Test
    @DisplayName("""
        Algorithm should return set of products with one that user most frequently purchase, if there are many should pick one with best client rate. 
        Next should return most frequently searched product.
        Then it should return products depending on desirability level.
        After that, it should return result coming solely from category preferences.
        If there is still too little products, it should pick some from product supplier, which supplies products from
        different criteria, like cheapest, newest.
        Test case asks for result set with lots of products, much more than is needed in frontend.
        """)
    void findByRecommendation_all_test() {
        //given
        Account givenAccount = Account.builder().login("testLogin").build();
        Category categoryWithWorstPoints = OrdersModuleTestData.getDefaultCategoryBuilder()
            .name("Worst")
            .build();
        Category categoryWithBestPoints = OrdersModuleTestData.getDefaultCategoryBuilder()
            .name("Best")
            .build();

        Product productPurchasedByClientWithNotBestRate = OrdersModuleTestData.buildDefaultProduct();
        Product productPurchasedByClientWithoutRate = OrdersModuleTestData.buildDefaultProduct();
        Product productPurchasedByClientWithBestRate = OrdersModuleTestData.buildDefaultProduct();
        Product searchedProductWithoutSearchedCategory = OrdersModuleTestData.buildDefaultProduct();
        Product searchedProductWithBestProductPointsButWorstCategoryPoints = OrdersModuleTestData.getDefaultProductBuilder()
                .category(categoryWithWorstPoints)
                .build();
        Product searchedProductWithWorstProductPointsButBestCategoryPoints = OrdersModuleTestData.getDefaultProductBuilder()
                .category(categoryWithBestPoints)
                .build();
        Product notSearchedProductWithSearchedCategoryWithWorstPoints = OrdersModuleTestData.getDefaultProductBuilder()
                .category(categoryWithWorstPoints)
                .build();
        Product notSearchedProductWithSearchedCategoryWithBestPoints = OrdersModuleTestData.getDefaultProductBuilder()
                .category(categoryWithBestPoints)
                .build();
        Product bestRatedProduct1 = OrdersModuleTestData.buildDefaultProduct();
        Product bestRatedProduct2 = OrdersModuleTestData.buildDefaultProduct();
        Product newestProduct = OrdersModuleTestData.buildDefaultProduct();
        Product cheapestProduct1 = OrdersModuleTestData.buildDefaultProduct();
        Product cheapestProduct2 = OrdersModuleTestData.buildDefaultProduct();
        Product productThatIsRunningOut = OrdersModuleTestData.buildDefaultProduct();
        Product availableProduct1 = OrdersModuleTestData.buildDefaultProduct();
        Product availableProduct2 = OrdersModuleTestData.buildDefaultProduct();

        OrderedProduct orderedProductWithNotBestRate = OrderedProduct.builder()
            .product(productPurchasedByClientWithNotBestRate)
            .rate(Rate.builder().value(2).build())
            .build();

        OrderedProduct orderedProductWithoutRate = OrderedProduct.builder()
            .product(productPurchasedByClientWithoutRate)
            .build();

        OrderedProduct orderedProductWithBestRate = OrderedProduct.builder()
            .product(productPurchasedByClientWithBestRate)
            .rate(Rate.builder().value(5).build())
            .build();

        Map<Long, Double> productPreferences = Map.ofEntries(
            entry(searchedProductWithoutSearchedCategory.getId(), 60.0),                       // 60
            entry(searchedProductWithBestProductPointsButWorstCategoryPoints.getId(), 70.0),   // 70 + 10 = 80
            entry(searchedProductWithWorstProductPointsButBestCategoryPoints.getId(), 30.0)    // 30 + 55.0 = 85.0
        );
        Map<String, Double> categoryPreferences = Map.ofEntries(
            entry(categoryWithWorstPoints.getName(), 10.0),
            entry(categoryWithBestPoints.getName(), 55.0)
        );
        UserPreferences preferences = new UserPreferences(categoryPreferences, productPreferences);

        Map<Long, Product> productMap = Map.ofEntries(
            entry(searchedProductWithoutSearchedCategory.getId(), searchedProductWithoutSearchedCategory),
            entry(searchedProductWithBestProductPointsButWorstCategoryPoints.getId(), searchedProductWithBestProductPointsButWorstCategoryPoints),
            entry(searchedProductWithWorstProductPointsButBestCategoryPoints.getId(), searchedProductWithWorstProductPointsButBestCategoryPoints)
        );

        given(accountRepository.findByLogin(givenAccount.getLogin()))
            .willReturn(Optional.of(givenAccount));

        given(productRepository.findTheMostFrequentlyPurchasedProducts(givenAccount.getLogin()))
            .willReturn(List.of(orderedProductWithoutRate, orderedProductWithNotBestRate, orderedProductWithBestRate));

        given(productRepository.findById(anyLong()))
            .willAnswer(invocationOnMock -> {
                Long id = invocationOnMock.getArgument(0);
                return Optional.ofNullable(productMap.get(id));
            });

        given(productRepository.findProductsByIds(anySet()))
            .willReturn(productMap.values().stream().toList());

        Map<String, Category> categoryMap = Map.ofEntries(
            entry(categoryWithWorstPoints.getName(), categoryWithWorstPoints),
            entry(categoryWithBestPoints.getName(), categoryWithBestPoints)
        );

        Map<Category, List<Product>> categoryProductsMap = Map.ofEntries(
            entry(categoryWithWorstPoints, List.of(
                searchedProductWithBestProductPointsButWorstCategoryPoints,
                notSearchedProductWithSearchedCategoryWithWorstPoints
            )),
            entry(categoryWithBestPoints, List.of(
                searchedProductWithWorstProductPointsButBestCategoryPoints,
                notSearchedProductWithSearchedCategoryWithBestPoints
            ))
        );

        given(accountRepository.findByLogin(givenAccount.getLogin()))
            .willReturn(Optional.of(givenAccount));

        given(categoryRepository.findByName(anyString()))
            .willAnswer(invocationOnMock -> {
                String categoryName = invocationOnMock.getArgument(0);
                return Optional.ofNullable(categoryMap.get(categoryName));
            });

        given(productRepository.findByCategory(any(Category.class)))
            .willAnswer(invocationOnMock -> {
                Category category = invocationOnMock.getArgument(0);
                return categoryProductsMap.get(category);
            });

        given(productRepository.findBestRatedProducts())
            .willReturn(List.of(bestRatedProduct1, bestRatedProduct2));

        given(productRepository.findNewestProducts())
            .willReturn(List.of(newestProduct));

        given(productRepository.findCheapestProducts())
            .willReturn(List.of(cheapestProduct1, cheapestProduct2));

        given(productRepository.findProductsThatAreRunningOut())
            .willReturn(List.of(productThatIsRunningOut));

        given(productRepository.findAvailableProducts())
            .willReturn(List.of(availableProduct1, availableProduct2));

        //when
        List<Product> result = underTest.findByRecommendation(givenAccount.getLogin(), preferences, 14);

        //then
        assertThat(result)
            .containsExactly(
                productPurchasedByClientWithBestRate,
                searchedProductWithBestProductPointsButWorstCategoryPoints,
                searchedProductWithWorstProductPointsButBestCategoryPoints,
                searchedProductWithoutSearchedCategory,
                notSearchedProductWithSearchedCategoryWithBestPoints,
                notSearchedProductWithSearchedCategoryWithWorstPoints,
                bestRatedProduct1,
                bestRatedProduct2,
                newestProduct,
                cheapestProduct1,
                cheapestProduct2,
                productThatIsRunningOut,
                availableProduct1,
                availableProduct2
            )
            .hasSize(14);

        // some records in steps should be omitted because they were added before and we return collection without duplicates
    }

    @Test
    @DisplayName("""
        Algorithm should return set of products with one that user most frequently purchase, if there are many should pick one with best client rate. 
        Next should return most frequently searched product.
        Then it should return products depending on desirability level.
        After that, it should return result coming solely from category preferences.
        If there is still too little products, it should pick some from product supplier, which supplies products from
        different criteria, like cheapest, newest.
        Test case shows real life situation where we have lots of products to choose but we ask only for few.
        Algorithm should return only most important.
        """)
    void findByRecommendation_all_test2() {
        //given
        Account givenAccount = Account.builder().login("testLogin").build();
        Category categoryWithWorstPoints = OrdersModuleTestData.getDefaultCategoryBuilder()
            .name("Worst")
            .build();
        Category categoryWithBestPoints = OrdersModuleTestData.getDefaultCategoryBuilder()
            .name("Best")
            .build();

        Product productPurchasedByClientWithNotBestRate = OrdersModuleTestData.buildDefaultProduct();
        Product productPurchasedByClientWithoutRate = OrdersModuleTestData.buildDefaultProduct();
        Product productPurchasedByClientWithBestRate = OrdersModuleTestData.buildDefaultProduct();
        Product searchedProductWithoutSearchedCategory = OrdersModuleTestData.buildDefaultProduct();
        Product searchedProductWithBestProductPointsButWorstCategoryPoints = OrdersModuleTestData.getDefaultProductBuilder()
            .category(categoryWithWorstPoints)
            .build();
        Product searchedProductWithWorstProductPointsButBestCategoryPoints = OrdersModuleTestData.getDefaultProductBuilder()
            .category(categoryWithBestPoints)
            .build();
        Product notSearchedProductWithSearchedCategoryWithWorstPoints = OrdersModuleTestData.getDefaultProductBuilder()
            .category(categoryWithWorstPoints)
            .build();
        Product notSearchedProductWithSearchedCategoryWithBestPoints = OrdersModuleTestData.getDefaultProductBuilder()
            .category(categoryWithBestPoints)
            .build();
        Product bestRatedProduct1 = OrdersModuleTestData.buildDefaultProduct();
        Product bestRatedProduct2 = OrdersModuleTestData.buildDefaultProduct();
        Product newestProduct = OrdersModuleTestData.buildDefaultProduct();
        Product cheapestProduct1 = OrdersModuleTestData.buildDefaultProduct();
        Product cheapestProduct2 = OrdersModuleTestData.buildDefaultProduct();
        Product productThatIsRunningOut = OrdersModuleTestData.buildDefaultProduct();
        Product availableProduct1 = OrdersModuleTestData.buildDefaultProduct();
        Product availableProduct2 = OrdersModuleTestData.buildDefaultProduct();

        OrderedProduct orderedProductWithNotBestRate = OrderedProduct.builder()
            .product(productPurchasedByClientWithNotBestRate)
            .rate(Rate.builder().value(2).build())
            .build();

        OrderedProduct orderedProductWithoutRate = OrderedProduct.builder()
            .product(productPurchasedByClientWithoutRate)
            .build();

        OrderedProduct orderedProductWithBestRate = OrderedProduct.builder()
            .product(productPurchasedByClientWithBestRate)
            .rate(Rate.builder().value(5).build())
            .build();

        Map<Long, Double> productPreferences = Map.ofEntries(
            entry(searchedProductWithoutSearchedCategory.getId(), 60.0),                       // 60
            entry(searchedProductWithBestProductPointsButWorstCategoryPoints.getId(), 70.0),   // 70 + 10 = 80
            entry(searchedProductWithWorstProductPointsButBestCategoryPoints.getId(), 30.0)    // 30 + 55.0 = 85.0
        );
        Map<String, Double> categoryPreferences = Map.ofEntries(
            entry(categoryWithWorstPoints.getName(), 10.0),
            entry(categoryWithBestPoints.getName(), 55.0)
        );
        UserPreferences preferences = new UserPreferences(categoryPreferences, productPreferences);

        Map<Long, Product> productMap = Map.ofEntries(
            entry(searchedProductWithoutSearchedCategory.getId(), searchedProductWithoutSearchedCategory),
            entry(searchedProductWithBestProductPointsButWorstCategoryPoints.getId(), searchedProductWithBestProductPointsButWorstCategoryPoints),
            entry(searchedProductWithWorstProductPointsButBestCategoryPoints.getId(), searchedProductWithWorstProductPointsButBestCategoryPoints)
        );

        given(accountRepository.findByLogin(givenAccount.getLogin()))
            .willReturn(Optional.of(givenAccount));

        given(productRepository.findTheMostFrequentlyPurchasedProducts(givenAccount.getLogin()))
            .willReturn(List.of(orderedProductWithoutRate, orderedProductWithNotBestRate, orderedProductWithBestRate));

        given(productRepository.findById(anyLong()))
            .willAnswer(invocationOnMock -> {
                Long id = invocationOnMock.getArgument(0);
                return Optional.ofNullable(productMap.get(id));
            });

        given(productRepository.findProductsByIds(anySet()))
            .willReturn(productMap.values().stream().toList());

        Map<String, Category> categoryMap = Map.ofEntries(
            entry(categoryWithWorstPoints.getName(), categoryWithWorstPoints),
            entry(categoryWithBestPoints.getName(), categoryWithBestPoints)
        );

        Map<Category, List<Product>> categoryProductsMap = Map.ofEntries(
            entry(categoryWithWorstPoints, List.of(
                searchedProductWithBestProductPointsButWorstCategoryPoints,
                notSearchedProductWithSearchedCategoryWithWorstPoints
            )),
            entry(categoryWithBestPoints, List.of(
                searchedProductWithWorstProductPointsButBestCategoryPoints,
                notSearchedProductWithSearchedCategoryWithBestPoints
            ))
        );

        given(accountRepository.findByLogin(givenAccount.getLogin()))
            .willReturn(Optional.of(givenAccount));

        given(categoryRepository.findByName(anyString()))
            .willAnswer(invocationOnMock -> {
                String categoryName = invocationOnMock.getArgument(0);
                return Optional.ofNullable(categoryMap.get(categoryName));
            });

        given(productRepository.findByCategory(any(Category.class)))
            .willAnswer(invocationOnMock -> {
                Category category = invocationOnMock.getArgument(0);
                return categoryProductsMap.get(category);
            });

        //when
        List<Product> result = underTest.findByRecommendation(givenAccount.getLogin(), preferences, 6);

        //then
        assertThat(result)
            .containsExactly(
                productPurchasedByClientWithBestRate,
                searchedProductWithBestProductPointsButWorstCategoryPoints,
                searchedProductWithWorstProductPointsButBestCategoryPoints,
                searchedProductWithoutSearchedCategory,
                notSearchedProductWithSearchedCategoryWithBestPoints,
                notSearchedProductWithSearchedCategoryWithWorstPoints
            )
            .doesNotContain(
                bestRatedProduct1,
                bestRatedProduct2,
                newestProduct,
                cheapestProduct1,
                cheapestProduct2,
                productThatIsRunningOut,
                availableProduct1,
                availableProduct2
            )
            .hasSize(6);

        // some records in steps should be omitted because they were added before and we return collection without duplicates
    }
}