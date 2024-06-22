package pl.lodz.p.edu.shop.logic.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.edu.shop.dataaccess.dao.api.CategoryDAO;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Category;
import pl.lodz.p.edu.shop.dataaccess.model.other.Constraint;
import pl.lodz.p.edu.shop.dataaccess.repository.api.CategoryRepository;
import pl.lodz.p.edu.shop.exception.ApplicationExceptionFactory;
import pl.lodz.p.edu.shop.logic.service.api.CategoryService;

import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

@Slf4j

@Service
@Transactional(transactionManager = "ordersModTxManager", propagation = Propagation.REQUIRES_NEW)
@Qualifier("CategoryServiceImpl")
class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryDAO categoryDAO;

    CategoryServiceImpl(CategoryRepository categoryRepository, CategoryDAO categoryDAO) {
        requireNonNull(categoryRepository, "Category service requires non null category repository");
        requireNonNull(categoryDAO, "Category service requires non null category DAO");

        this.categoryRepository = categoryRepository;
        this.categoryDAO = categoryDAO;
    }

    @Override
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Map<String, Object>> findSchemaByCategoryName(String category) {
        String tableName = "%ss".formatted(category.toLowerCase());
        List<Map<String, Object>> tableSchema = categoryDAO.findTableSchema(tableName);

        if (tableSchema.isEmpty()) {
            throw ApplicationExceptionFactory.createSchemaNotFoundException();
        }

        return tableSchema;
    }

    @Override
    public Category createCategory(String category, Map<String, List<String>> schema) {
        try {
            Category newCategory = Category.builder()
                .name(category)
                .build();
            String tableName = "%ss".formatted(category.toLowerCase());

            Map<String, List<Constraint>> dbSchema = schema.entrySet().stream()
                .collect(toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().stream()
                        .map(Constraint::valueOf)
                        .toList()
                ));

            categoryDAO.createTable(tableName, dbSchema);
            return categoryRepository.save(newCategory);

        } catch (DataAccessException e) {
            log.warn("DataAccessException: ", e);
            throw ApplicationExceptionFactory.createUnknownException();
        }
    }
}
