package pl.lodz.p.edu.shop.presentation.adapter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Product;
import pl.lodz.p.edu.shop.logic.service.api.ProductService;
import pl.lodz.p.edu.shop.presentation.adapter.api.ProductServiceOperations;
import pl.lodz.p.edu.shop.presentation.dto.product.InputProductDto;
import pl.lodz.p.edu.shop.presentation.dto.product.ProductOutputDto;
import pl.lodz.p.edu.shop.presentation.mapper.api.ProductMapper;

import java.util.List;

@RequiredArgsConstructor

@Component
public class ProductServiceAdapter implements ProductServiceOperations {

    private final ProductService productService;
    private final ProductMapper productMapper;

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
}
