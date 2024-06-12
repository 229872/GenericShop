package pl.lodz.p.edu.shop.dataaccess.repository.module.order;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Product;
import pl.lodz.p.edu.shop.dataaccess.repository.api.ProductRepository;

interface ProductJpaRepository extends JpaRepository<Product, Long>, ProductRepository {
}
