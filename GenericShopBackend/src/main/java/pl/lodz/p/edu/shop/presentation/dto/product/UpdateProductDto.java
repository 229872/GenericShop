package pl.lodz.p.edu.shop.presentation.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateProductDto(
    @NotNull @Positive
    BigDecimal price,
    @NotNull @Positive
    Integer quantity,
    String imageUrl,
    @NotBlank
    String version
) {
}
