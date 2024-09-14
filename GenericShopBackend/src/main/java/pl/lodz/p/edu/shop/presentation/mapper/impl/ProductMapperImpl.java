package pl.lodz.p.edu.shop.presentation.mapper.impl;

import org.springframework.stereotype.Component;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Category;
import pl.lodz.p.edu.shop.dataaccess.model.entity.OrderedProduct;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Product;
import pl.lodz.p.edu.shop.presentation.dto.product.InputProductDto;
import pl.lodz.p.edu.shop.presentation.dto.product.ProductOutputDto;
import pl.lodz.p.edu.shop.presentation.mapper.api.ProductMapper;
import pl.lodz.p.edu.shop.util.SecurityUtil;
import pl.lodz.p.edu.shop.util.TextUtil;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public Product mapToProduct(InputProductDto inputProductDto) {
        Map<String, Object> categoryProperties = inputProductDto.categoryProperties();
        Map<String, Object> validProperties = categoryProperties.entrySet().stream()
            .collect(toMap(
                entry -> TextUtil.toSnakeCase(entry.getKey()),
                Map.Entry::getValue
            ));

        Category category = Category.builder()
            .name(inputProductDto.categoryName())
            .build();

        String imageUrl = inputProductDto.imageUrl().isBlank() ? null : inputProductDto.imageUrl();

        return Product.builder()
            .name(inputProductDto.name())
            .price(inputProductDto.price())
            .quantity(inputProductDto.quantity())
            .category(category)
            .imageUrl(imageUrl)
            .tableProperties(validProperties)
            .build();
    }

    @Override
    public ProductOutputDto mapToProductOutputDtoWithoutVersion(Product product) {
        return ProductOutputDto.builder()
            .id(product.getId())
            .archival(product.isArchival())
            .name(product.getName())
            .price(product.getPrice())
            .quantity(product.getQuantity())
            .imageUrl(product.getImageUrl())
            .build();
    }

    @Override
    public ProductOutputDto mapToProductOutputDtoWithoutVersion(OrderedProduct product) {
        return ProductOutputDto.builder()
            .id(product.getId())
            .name(product.getName())
            .price(product.getPrice())
            .quantity(product.getQuantity())
            .build();
    }

    @Override
    public ProductOutputDto mapToProductOutputDtoWithVersion(Product product) {
        String combinedVersion = SecurityUtil.signVersion(product.getVersion());
        Map<String, Object> tableProperties = product.getTableProperties();
        Map<String, Object> mappedProperties = tableProperties.entrySet().stream()
            .collect(toMap(
                entry -> TextUtil.toCamelCase(entry.getKey()),
                Map.Entry::getValue
            ));

        return ProductOutputDto.builder()
            .id(product.getId())
            .version(combinedVersion)
            .archival(product.isArchival())
            .name(product.getName())
            .price(product.getPrice())
            .quantity(product.getQuantity())
            .imageUrl(product.getImageUrl())
            .categoryProperties(mappedProperties)
            .build();
    }
}
