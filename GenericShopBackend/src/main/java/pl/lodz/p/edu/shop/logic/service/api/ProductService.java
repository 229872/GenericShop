package pl.lodz.p.edu.shop.logic.service.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    List<Product> findAll();

    Page<Product> findAll(Pageable pageable);

    Page<Product> findByCategory(Pageable pageable, String categoryName);

    Product findById(Long id);

    Product findByIdShort(Long id);

    Product create(Product product);

    Product update(Long id, BigDecimal newPrice, Integer newQuantity, String newImageUrl, String frontendVersion);

    Product archive(Long id);
}
