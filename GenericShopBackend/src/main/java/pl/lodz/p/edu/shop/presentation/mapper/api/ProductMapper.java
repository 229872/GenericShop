package pl.lodz.p.edu.shop.presentation.mapper.api;

import pl.lodz.p.edu.shop.dataaccess.model.entity.Product;
import pl.lodz.p.edu.shop.presentation.dto.product.InputProductDto;
import pl.lodz.p.edu.shop.presentation.dto.product.ProductOutputDto;

public interface ProductMapper {

    Product mapToProduct(InputProductDto inputProductDto);

    ProductOutputDto mapToProductOutputDtoWithoutVersion(Product product);

    ProductOutputDto mapToProductOutputDtoWithVersion(Product product);
}
