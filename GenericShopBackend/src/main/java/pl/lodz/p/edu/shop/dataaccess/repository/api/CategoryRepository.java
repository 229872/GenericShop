package pl.lodz.p.edu.shop.dataaccess.repository.api;

import pl.lodz.p.edu.shop.dataaccess.model.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    List<Category> findAll();

    Optional<Category> findById(Long id);

    Optional<Category> findByName(String name);

    Category save(Category category);

    void flush();
}
