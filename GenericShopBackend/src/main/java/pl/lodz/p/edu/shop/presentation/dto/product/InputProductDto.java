package pl.lodz.p.edu.shop.presentation.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import pl.lodz.p.edu.shop.presentation.validation.annotation.TableName;

import java.math.BigDecimal;
import java.util.Map;

public record InputProductDto(
    @NotBlank
    String name,
    @NotNull @Positive
    BigDecimal price,
    @NotNull @Positive
    Integer quantity,
    @TableName
    String categoryName,
    String imageUrl,
    @NotNull
    Map<String, Object> categoryProperties
) {
}
