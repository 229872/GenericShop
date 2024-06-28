package pl.lodz.p.edu.shop.dataaccess.repository.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    List<Product> findAll();

    Page<Product> findAll(Pageable pageable);

    Optional<Product> findById(Long id);

    Product save(Product product);

    Product saveAndFlush(Product product);

}
