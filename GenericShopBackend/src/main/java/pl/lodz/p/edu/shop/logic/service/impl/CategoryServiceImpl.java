package pl.lodz.p.edu.shop.logic.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
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
import pl.lodz.p.edu.shop.exception.SystemExceptionFactory;
import pl.lodz.p.edu.shop.logic.service.api.CategoryService;
import pl.lodz.p.edu.shop.util.ExceptionUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    public Category createCategory(Category category, Map<String, List<String>> schema) {
        try {
            String tableName = category.getCategoryTableName();

            Map<String, List<Constraint>> dbSchema = schema.entrySet().stream()
                .collect(toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().stream()
                        .map(Constraint::valueOf)
                        .toList()
                ));

            Category result = categoryRepository.save(category);
            //Flush to get exception and handle it in logic layer
            categoryRepository.flush();
            categoryDAO.createTable(tableName, dbSchema);
            return result;

        } catch (DataAccessException e) {
            log.warn("DataAccessException: ", e);
            var violationException = ExceptionUtil.findCause(e, ConstraintViolationException.class);

            if (Objects.nonNull(violationException) && Objects.nonNull(violationException.getConstraintName())) {
                return handleConstraintViolationException(violationException);
            }

            throw ApplicationExceptionFactory.createUnknownException();
        }
    }


    private Category handleConstraintViolationException(ConstraintViolationException e) {
        switch (Objects.requireNonNull(e.getConstraintName())) {
            case "categories_name_key" -> throw ApplicationExceptionFactory.createCategoryConflictException();
            default -> throw SystemExceptionFactory.createDbConstraintViolationException(e);
        }
    }
}
