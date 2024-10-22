package pl.lodz.p.edu.shop.dataaccess.repository.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Category;
import pl.lodz.p.edu.shop.dataaccess.model.entity.OrderedProduct;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository {

    List<Product> findAll();

    Page<Product> findAll(Pageable pageable);

    List<Product> findByCategory(Category category);

    Page<Product> findByCategory(Category category, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isArchival = false AND p.id IN :ids")
    List<Product> findProductsByIds(@Param("ids") Set<Long> productIds);

    @Query("SELECT p FROM Product p WHERE p.isArchival = false AND p.category.name IN :categoryNames")
    List<Product> findProductsByCategories(@Param("categoryNames") Collection<String> categoryNames);

    @Query("SELECT p FROM OrderedProduct p WHERE p.id = :id")
    Optional<OrderedProduct> findOrderedProductById(@Param("id") Long id);

    @Query("WITH product_counts AS (" +
        "   SELECT p.product AS product, COUNT(p.product) AS productCount " +
        "   FROM OrderedProduct p " +
        "   WHERE p.account.login = :login " +
        "   GROUP BY p.product" +
        "), max_count AS (" +
        "   SELECT MAX(productCount) AS maxProductCount " +
        "   FROM product_counts" +
        ")" +
        "SELECT op " +
        "FROM OrderedProduct op " +
        "WHERE op.account.login = :login " +
        "AND op.product IN (" +
        "   SELECT pc.product " +
        "   FROM product_counts pc, max_count mc " +
        "   WHERE pc.productCount = mc.maxProductCount" +
        ")")
    List<OrderedProduct> findTheMostFrequentlyPurchasedProducts(String login);

    @Query("SELECT p.product FROM OrderedProduct p WHERE p.account.login = :login AND p.rate IS NOT NULL GROUP BY p.product ORDER BY MAX(p.rate.value)")
    Page<Product> findBestRatedProductsByTheUser(String login, Pageable pageable);

    Optional<Product> findById(Long id);

    Product save(Product product);

    Product saveAndFlush(Product product);

}
