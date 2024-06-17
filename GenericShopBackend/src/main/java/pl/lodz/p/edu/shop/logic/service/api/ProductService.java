package pl.lodz.p.edu.shop.logic.service.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Category;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Product;

import java.util.List;

public interface ProductService {

    List<Product> findAll();

    Page<Product> findAll(Pageable pageable);

    Product findById(Long id);

    Product create(Product product);

    Product update(Long id, Product newProduct);

    Product archive(Long id);

    List<Category> findAllCategories();
}
