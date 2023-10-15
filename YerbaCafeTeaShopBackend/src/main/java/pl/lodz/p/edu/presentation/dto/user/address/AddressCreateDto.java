package pl.lodz.p.edu.presentation.dto.user.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AddressCreateDto(
    @NotBlank
    String postalCode,
    @NotBlank
    String country,
    @NotBlank
    String city,
    @NotBlank
    String street,
    @NotNull
    Integer houseNumber
) {
}
