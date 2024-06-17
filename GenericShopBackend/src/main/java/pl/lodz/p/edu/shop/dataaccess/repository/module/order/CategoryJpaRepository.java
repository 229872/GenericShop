package pl.lodz.p.edu.shop.dataaccess.repository.module.order;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Category;
import pl.lodz.p.edu.shop.dataaccess.repository.api.CategoryRepository;

interface CategoryJpaRepository extends JpaRepository<Category, Long>, CategoryRepository {
}
