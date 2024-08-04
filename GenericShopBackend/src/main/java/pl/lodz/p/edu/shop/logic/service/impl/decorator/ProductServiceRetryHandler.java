package pl.lodz.p.edu.shop.logic.service.impl.decorator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.RequestScope;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Product;
import pl.lodz.p.edu.shop.logic.service.api.ProductService;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequestScope
@Primary
@Transactional(transactionManager = "ordersModTxManager", propagation = Propagation.NEVER)
@Qualifier("ProductServiceRetryHandler")
public class ProductServiceRetryHandler extends AbstractRetryHandler implements ProductService {

    private final ProductService productService;

    public ProductServiceRetryHandler(@Qualifier("ProductServiceImpl") ProductService productService) {
        this.productService = productService;
    }

    @Override
    public List<Product> findAll() {
        return repeatTransactionWhenTimeoutOccurred(() -> productService.findAll());
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return repeatTransactionWhenTimeoutOccurred(() -> productService.findAll(pageable));
    }

    @Override
    public Page<Product> findByCategory(Pageable pageable, String categoryName) {
        return repeatTransactionWhenTimeoutOccurred(() -> productService.findByCategory(pageable, categoryName));
    }

    @Override
    public Product findById(Long id) {
        return repeatTransactionWhenTimeoutOccurred(() -> productService.findById(id));
    }

    @Override
    public Product findByIdShort(Long id) {
        return repeatTransactionWhenTimeoutOccurred(() -> productService.findByIdShort(id));
    }

    @Override
    public Product create(Product product) {
        return repeatTransactionWhenTimeoutOccurred(() -> productService.create(product));
    }

    @Override
    public Product update(Long id, BigDecimal newPrice, Integer newQuantity, String newImageUrl, String frontendVersion) {
        return repeatTransactionWhenTimeoutOccurred(() -> productService.update(id, newPrice, newQuantity, newImageUrl,
            frontendVersion));
    }

    @Override
    public Product archive(Long id) {
        return repeatTransactionWhenTimeoutOccurred(() -> productService.archive(id));
    }
}
