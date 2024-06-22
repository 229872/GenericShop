package pl.lodz.p.edu.shop.presentation.adapter.api;

import pl.lodz.p.edu.shop.presentation.dto.product.ProductSchemaDTO;

import java.util.List;
import java.util.Map;

public interface CategoryServiceOperations {

    List<String> findAllCategories();

    List<Map<String, Object>> findSchemaByCategoryName(String name);

    void createCategory(ProductSchemaDTO productSchemaDTO);
}
