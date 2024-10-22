package pl.lodz.p.edu.shop.logic.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.edu.shop.dataaccess.dao.api.ProductDAO;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Category;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Product;
import pl.lodz.p.edu.shop.dataaccess.repository.api.CategoryRepository;
import pl.lodz.p.edu.shop.dataaccess.repository.api.ProductRepository;
import pl.lodz.p.edu.shop.exception.ApplicationExceptionFactory;
import pl.lodz.p.edu.shop.exception.SystemExceptionFactory;
import pl.lodz.p.edu.shop.logic.service.api.ProductService;
import pl.lodz.p.edu.shop.util.ExceptionUtil;
import pl.lodz.p.edu.shop.util.SecurityUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

@Slf4j

@Service
@Transactional(transactionManager = "ordersModTxManager", propagation = Propagation.REQUIRES_NEW)
@Qualifier("ProductServiceImpl")
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductDAO productDAO;
    private final CategoryRepository categoryRepository;


    public ProductServiceImpl(ProductRepository productRepository, ProductDAO productDAO, CategoryRepository categoryRepository) {
        requireNonNull(productRepository, "Product service requires non null product repository");
        requireNonNull(productDAO, "Product service requires non null product DAO");
        requireNonNull(categoryRepository, "Product service requires non null category repository");

        this.productRepository = productRepository;
        this.productDAO = productDAO;
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
    public Page<Product> findByCategory(Pageable pageable, String categoryName) {
        Category category = categoryRepository.findByName(categoryName)
            .orElseThrow(ApplicationExceptionFactory::createCategoryNotFoundException);

        return productRepository.findByCategory(category, pageable);
    }

    @Override
    public Product findById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createProductNotFoundException);
        Map<String, Object> categoryData = productDAO.findByIdInTable(id, product.getCategory().getCategoryTableName());
        product.setTableProperties(categoryData);
        return product;
    }

    @Override
    public Product findByIdShort(Long id) {
        return productRepository.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createProductNotFoundException);
    }

    @Override
    public Product create(Product product) {
        try {
            Category category = categoryRepository.findByName(product.getCategory().getName())
                .orElseThrow(ApplicationExceptionFactory::createCategoryNotFoundException);

            product.setCategory(category);
            Product result = productRepository.saveAndFlush(product);

            Map<String, Object> categoryProperties = product.getTableProperties();
            categoryProperties.put("product_id", result.getId());

            var resultProps = productDAO.insert(product.getCategory().getCategoryTableName(), categoryProperties);
            result.setTableProperties(resultProps);
            return result;

        } catch (DataAccessException e) {
            log.warn("DataAccessExceptions occurred: ", e);
            var violationException = ExceptionUtil.findCause(e, ConstraintViolationException.class);

            if (nonNull(violationException) && nonNull(violationException.getConstraintName())) {
                return handleConstraintViolationException(violationException);
            }

            throw ApplicationExceptionFactory.createUnknownException();
        }
    }

    @Override
    public Product update(Long id, BigDecimal newPrice, Integer newQuantity, String newImageUrl, String frontendVersion) {
        Product product = productRepository.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createProductNotFoundException);

        if (product.isArchival()) {
            throw ApplicationExceptionFactory.createCantModifyArchivalProductException();
        }

        long version = product.getVersion();

        if (!SecurityUtil.verifySignature(version, frontendVersion)) {
            throw ApplicationExceptionFactory.createApplicationOptimisticLockException();
        }

        product.setPrice(newPrice);
        product.setQuantity(newQuantity);
        product.setImageUrl(newImageUrl);

        return save(product);
    }


    @Override
    public Product archive(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createProductNotFoundException);

        product.setArchival(true);

        return save(product);
    }

    private Product save(Product product) {
        try {
            //Flush to get exception and handle it in logic layer
            productRepository.saveAndFlush(product);
            return product;

        } catch (DataAccessException e) {
            var violationException = ExceptionUtil.findCause(e, ConstraintViolationException.class);

            if (nonNull(violationException) && nonNull(violationException.getConstraintName())) {
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
