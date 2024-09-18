package pl.lodz.p.edu.shop.presentation.dto.order;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RateInputDto(
    @Min(1) @Max(5) @NotNull
    Integer rateValue
) {
}

