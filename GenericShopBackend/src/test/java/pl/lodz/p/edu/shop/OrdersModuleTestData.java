package pl.lodz.p.edu.shop;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Category;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrdersModuleTestData {
    static long productCounter = 0;
    static long categoryCounter = 0;

    public static final String defaultProductName = "Product";
    public static final Integer defaultProductQuantity = 3;
    public static final BigDecimal defaultProductPrice = new BigDecimal("33.0");
    public static final Double defaultAverageRating = 3.5;
    public static final Category defaultCategory = Category.builder().name("Test").build();
    public static final Boolean defaultArchival = false;
    public static final String defaultCreatedBy = "testUser";

    public static final String defaultCategoryName = "Category";

    public static Product.ProductBuilder<?,?> getDefaultProductBuilder() {
        productCounter++;
        return Product.builder()
            .id(productCounter)
            .name(defaultProductName + productCounter)
            .quantity(defaultProductQuantity)
            .price(defaultProductPrice)
            .isArchival(defaultArchival)
            .createdBy(defaultCreatedBy)
            .averageRating(defaultAverageRating)
            .category(defaultCategory)
            .createdAt(LocalDateTime.now());
    }

    public static Product buildDefaultProduct() {
        return getDefaultProductBuilder()
            .build();
    }

    public static Category.CategoryBuilder<?,?> getDefaultCategoryBuilder() {
        categoryCounter++;
        return Category.builder()
            .id(categoryCounter)
            .name(defaultCategoryName + categoryCounter);
    }

    public static Category buildDefaultCategory() {
        return getDefaultCategoryBuilder()
            .build();
    }

    public static void resetCounter() {
        productCounter = 1;
    }

}
