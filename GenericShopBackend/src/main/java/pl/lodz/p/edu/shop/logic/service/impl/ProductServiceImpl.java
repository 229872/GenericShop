package pl.lodz.p.edu.shop.logic.service.impl;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Category;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Product;
import pl.lodz.p.edu.shop.dataaccess.repository.api.CategoryRepository;
import pl.lodz.p.edu.shop.dataaccess.repository.api.ProductRepository;
import pl.lodz.p.edu.shop.exception.ApplicationExceptionFactory;
import pl.lodz.p.edu.shop.exception.SystemExceptionFactory;
import pl.lodz.p.edu.shop.logic.service.api.ProductService;
import pl.lodz.p.edu.shop.util.ExceptionUtil;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@Service
@Transactional(transactionManager = "ordersModTxManager", propagation = Propagation.REQUIRES_NEW)
@Qualifier("ProductServiceImpl")
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        requireNonNull(productRepository, "Product service requires non null product repository");
        requireNonNull(categoryRepository, "Product service requires non null category repository");
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createProductNotFoundException);
    }

    @Override
    public Product create(Product product) {
        return save(product);
    }

    @Override
    public Product update(Long id, Product newProduct) {
        Product product = productRepository.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createProductNotFoundException);

        product.setPrice(newProduct.getPrice());
        product.setName(newProduct.getName());
        product.setQuantity(newProduct.getQuantity());
        product.setImageUrl(newProduct.getImageUrl());

        return save(product);
    }

    @Override
    public Product archive(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createProductNotFoundException);

        product.setArchival(true);

        return save(product);
    }

    @Override
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    protected Product save(Product product) {
        try {
            productRepository.save(product);
            productRepository.flush();
            return product;

        } catch (DataAccessException e) {
            var violationException = ExceptionUtil.findCause(e, ConstraintViolationException.class);

            if (Objects.nonNull(violationException) && Objects.nonNull(violationException.getConstraintName())) {
                return handleConstraintViolationException(violationException);
            }

            throw ApplicationExceptionFactory.createUnknownException();
        }
    }

    private Product handleConstraintViolationException(ConstraintViolationException e) {
        switch (Objects.requireNonNull(e.getConstraintName())) {
            default -> throw SystemExceptionFactory.createDbConstraintViolationException(e);
        }
    }
}
