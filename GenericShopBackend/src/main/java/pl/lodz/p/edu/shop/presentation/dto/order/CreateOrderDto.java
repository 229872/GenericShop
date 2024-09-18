package pl.lodz.p.edu.shop.presentation.dto.order;

import pl.lodz.p.edu.shop.presentation.dto.product.ProductRequest;
import pl.lodz.p.edu.shop.presentation.validation.annotation.ProductForOrderList;

import java.util.List;

public record CreateOrderDto(
    @ProductForOrderList
    List<ProductRequest> productsRequest
) {
}
