package pl.lodz.p.edu.shop.presentation.dto.order;

import lombok.Builder;
import pl.lodz.p.edu.shop.presentation.dto.product.ProductOutputDto;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record OrderOutputDto(
    Long id,
    String version,
    BigDecimal totalPrice,
    List<ProductOutputDto> products,
    Long accountId,
    String creationDate,
    Integer productQuantity
) {
}
