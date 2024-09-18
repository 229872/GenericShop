package pl.lodz.p.edu.shop.logic.service.impl.decorator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.RequestScope;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Category;
import pl.lodz.p.edu.shop.logic.service.api.CategoryService;

import java.util.List;
import java.util.Map;

@Service
@RequestScope
@Primary
@Transactional(transactionManager = "ordersModTxManager", propagation = Propagation.NEVER)
@Qualifier("CategoryServiceRetryHandler")
public class CategoryServiceRetryHandler extends AbstractRetryHandler implements CategoryService {

    private final CategoryService categoryService;

    public CategoryServiceRetryHandler(@Qualifier("CategoryServiceImpl") CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public List<Category> findAllCategories() {
        return repeatTransactionWhenTimeoutOccurred(categoryService::findAllCategories);
    }

    @Override
    public List<Map<String, Object>> findSchemaByCategoryName(String name) {
        return repeatTransactionWhenTimeoutOccurred(() -> categoryService.findSchemaByCategoryName(name));
    }

    @Override
    public Category createCategory(Category category, Map<String, List<String>> schema) {
        return repeatTransactionWhenTimeoutOccurred(() -> categoryService.createCategory(category, schema));
    }
}
