package pl.lodz.p.edu.shop.presentation.mapper.impl;

import org.springframework.stereotype.Component;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Category;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Product;
import pl.lodz.p.edu.shop.presentation.dto.product.InputProductDto;
import pl.lodz.p.edu.shop.presentation.dto.product.ProductOutputDto;
import pl.lodz.p.edu.shop.presentation.mapper.api.ProductMapper;
import pl.lodz.p.edu.shop.util.SecurityUtil;

import java.util.Map;

@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public Product mapToProduct(InputProductDto inputProductDto) {
        Map<String, Object> categoryProperties = inputProductDto.categoryProperties();
        Category category = Category.builder()
            .name(inputProductDto.categoryName())
            .build();

        return Product.builder()
            .name(inputProductDto.name())
            .price(inputProductDto.price())
            .quantity(inputProductDto.quantity())
            .category(category)
            .imageUrl(inputProductDto.imageUrl())
            .tableProperties(categoryProperties)
            .build();
    }

    @Override
    public ProductOutputDto mapToProductOutputDtoWithoutVersion(Product product) {
        String combinedVersion = SecurityUtil.signVersion(product.getVersion());

        return ProductOutputDto.builder()
            .id(product.getId())
            .version(combinedVersion)
            .archival(product.isArchival())
            .name(product.getName())
            .price(product.getPrice())
            .quantity(product.getQuantity())
            .imageUrl(product.getImageUrl())
            .build();
    }

    @Override
    public ProductOutputDto mapToProductOutputDtoWithVersion(Product product) {

        return ProductOutputDto.builder()
            .id(product.getId())
            .archival(product.isArchival())
            .name(product.getName())
            .price(product.getPrice())
            .quantity(product.getQuantity())
            .imageUrl(product.getImageUrl())
            .build();
    }
}
