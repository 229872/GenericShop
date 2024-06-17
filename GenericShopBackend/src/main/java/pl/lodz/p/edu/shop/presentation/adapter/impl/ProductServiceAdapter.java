package pl.lodz.p.edu.shop.presentation.adapter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Category;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Product;
import pl.lodz.p.edu.shop.logic.service.api.ProductService;
import pl.lodz.p.edu.shop.presentation.adapter.api.ProductServiceOperations;
import pl.lodz.p.edu.shop.presentation.dto.product.InputProductDto;
import pl.lodz.p.edu.shop.presentation.dto.product.ProductOutputDto;
import pl.lodz.p.edu.shop.presentation.mapper.api.ProductMapper;
import pl.lodz.p.edu.shop.presentation.mapper.api.SchemaMapper;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor

@Component
public class ProductServiceAdapter implements ProductServiceOperations {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final SchemaMapper schemaMapper;

    @Override
    public List<ProductOutputDto> findAll() {
        return productService.findAll().stream()
            .map(productMapper::mapToProductOutputDtoWithoutVersion)
            .toList();
    }

    @Override
    public Page<ProductOutputDto> findAll(Pageable pageable) {
        List<Sort.Order> orders = pageable.getSort().stream()
            .map(order -> order.getProperty().equals("archival") ?
                new Sort.Order(order.getDirection(), "isArchival") :
                order)
            .toList();
        Sort sort = Sort.by(orders);
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return productService.findAll(pageRequest)
            .map(productMapper::mapToProductOutputDtoWithoutVersion);
    }

    @Override
    public ProductOutputDto findById(Long id) {
        Product product = productService.findById(id);
        return productMapper.mapToProductOutputDtoWithVersion(product);
    }

    @Override
    public List<Map<String, Object>> findSchemaByCategoryName(String name) {
        return productService.findSchemaByCategoryName(name).stream()
            .map(schemaMapper::mapDbSchemaToApplicationSchema)
            .toList();
    }

    @Override
    public ProductOutputDto create(InputProductDto product) {
        Product newProductData = productMapper.mapToProduct(product);
        Product newProduct = productService.create(newProductData);
        return productMapper.mapToProductOutputDtoWithoutVersion(newProduct);
    }

    @Override
    public ProductOutputDto update(Long id, InputProductDto newProduct) {
        Product newProductData = productMapper.mapToProduct(newProduct);
        Product product = productService.update(id, newProductData);
        return productMapper.mapToProductOutputDtoWithoutVersion(product);
    }

    @Override
    public ProductOutputDto archive(Long id) {
        Product product = productService.archive(id);
        return productMapper.mapToProductOutputDtoWithoutVersion(product);
    }

    @Override
    public List<String> findAllCategories() {
        return productService.findAllCategories().stream()
            .map(Category::getName)
            .toList();
    }
}
