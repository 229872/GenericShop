package pl.lodz.p.edu.shop.logic.service.api;

import pl.lodz.p.edu.shop.dataaccess.model.entity.Category;

import java.util.List;
import java.util.Map;

public interface CategoryService {

    List<Category> findAllCategories();

    List<Map<String, Object>> findSchemaByCategoryName(String category);

    Category createCategory(String category, Map<String, List<String>> schema);
}
