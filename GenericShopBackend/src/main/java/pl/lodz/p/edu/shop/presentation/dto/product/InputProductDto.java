package pl.lodz.p.edu.shop.presentation.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record InputProductDto(
    @NotBlank
    String name,
    @NotNull @Positive
    BigDecimal price,
    @NotNull @Positive
    Integer quantity,
    @NotBlank
    String imageUrl
) {
}
