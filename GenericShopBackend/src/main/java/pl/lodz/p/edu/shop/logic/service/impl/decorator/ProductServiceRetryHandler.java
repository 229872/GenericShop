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
    public Product findById(Long id) {
        return repeatTransactionWhenTimeoutOccurred(() -> productService.findById(id));
    }

    @Override
    public Product create(Product product) {
        return repeatTransactionWhenTimeoutOccurred(() -> productService.create(product));
    }

    @Override
    public Product update(Long id, Product newProduct) {
        return repeatTransactionWhenTimeoutOccurred(() -> productService.update(id, newProduct));
    }

    @Override
    public Product archive(Long id) {
        return repeatTransactionWhenTimeoutOccurred(() -> productService.archive(id));
    }
}
