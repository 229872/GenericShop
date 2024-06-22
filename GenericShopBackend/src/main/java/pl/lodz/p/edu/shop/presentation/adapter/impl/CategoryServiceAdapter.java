package pl.lodz.p.edu.shop.presentation.adapter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Category;
import pl.lodz.p.edu.shop.logic.service.api.CategoryService;
import pl.lodz.p.edu.shop.presentation.adapter.api.CategoryServiceOperations;
import pl.lodz.p.edu.shop.presentation.dto.product.ProductSchemaDTO;
import pl.lodz.p.edu.shop.presentation.mapper.api.SchemaMapper;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor

@Component
public class CategoryServiceAdapter implements CategoryServiceOperations {

    private final CategoryService categoryService;
    private final SchemaMapper schemaMapper;

    @Override
    public List<String> findAllCategories() {
        return categoryService.findAllCategories().stream()
            .map(Category::getName)
            .toList();
    }

    @Override
    public List<Map<String, Object>> findSchemaByCategoryName(String name) {
        return categoryService.findSchemaByCategoryName(name).stream()
            .map(schemaMapper::mapDbSchemaToApplicationSchema)
            .toList();
    }

    @Override
    public void createCategory(ProductSchemaDTO productSchemaDTO) {
        String categoryName = productSchemaDTO.categoryName();
        String validCategory = categoryName.substring(0, 1).toUpperCase() + categoryName.substring(1);
        categoryService.createCategory(validCategory, productSchemaDTO.properties());
    }
}
