package pl.lodz.p.edu.shop.presentation.dto.product;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Builder
public record ProductOutputDto(
    Long id,
    String version,
    Boolean archival,
    String name,
    BigDecimal price,
    Integer quantity,
    String imageUrl,
    Double averageRating,
    Integer rate,
    Map<String, Object> categoryProperties
) {
}
