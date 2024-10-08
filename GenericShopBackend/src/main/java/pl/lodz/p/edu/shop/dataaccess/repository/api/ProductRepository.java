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

public interface ProductRepository {

    List<Product> findAll();

    Page<Product> findAll(Pageable pageable);

    Page<Product> findByCategory(Category category, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isArchival = false AND p.id IN :ids")
    List<Product> findProductsByIds(@Param("ids") List<Long> productIds);

    @Query("SELECT p FROM Product p WHERE p.isArchival = false AND p.category.name IN :categoryNames")
    List<Product> findProductsByCategories(@Param("categoryNames") List<String> categoryNames);

    @Query("SELECT p FROM OrderedProduct p WHERE p.id = :id")
    Optional<OrderedProduct> findOrderedProductById(@Param("id") Long id);

    Optional<Product> findById(Long id);

    Product save(Product product);

    Product saveAndFlush(Product product);

}
