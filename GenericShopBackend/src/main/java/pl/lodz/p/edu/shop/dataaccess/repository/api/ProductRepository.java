package pl.lodz.p.edu.shop.dataaccess.repository.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Category;
import pl.lodz.p.edu.shop.dataaccess.model.entity.OrderedProduct;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Product;

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

    @Query("SELECT p FROM Product p WHERE p.price = (SELECT MIN(p2.price) FROM Product p2 WHERE NOT p2.isArchival) AND NOT p.isArchival")
    List<Product> findCheapestProducts();

    @Query("SELECT p FROM Product p WHERE p.createdAt = (SELECT MAX(p2.createdAt) FROM Product p2 WHERE NOT p2.isArchival) AND NOT p.isArchival")
    List<Product> findNewestProducts();

    @Query("SELECT p FROM Product p WHERE p.quantity IN (1, 2) AND NOT p.isArchival")
    List<Product> findProductsThatAreRunningOut();

    @Query("SELECT p FROM Product p WHERE p.averageRating = (SELECT MAX(p2.averageRating) FROM Product p2 WHERE NOT p.isArchival) AND NOT p.isArchival")
    List<Product> findBestRatedProducts();

    @Query("SELECT p FROM Product p WHERE p.quantity > 0 AND NOT p.isArchival")
    List<Product> findAvailableProducts();

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

    @Query("SELECT p FROM OrderedProduct p WHERE p.id = :id")
    Optional<OrderedProduct> findOrderedProductById(@Param("id") Long id);

    Optional<Product> findById(Long id);

    Product save(Product product);

    Product saveAndFlush(Product product);

}
