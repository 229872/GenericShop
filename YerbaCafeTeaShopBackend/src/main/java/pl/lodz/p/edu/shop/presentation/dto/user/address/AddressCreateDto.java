package pl.lodz.p.edu.shop.presentation.dto.user.address;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import pl.lodz.p.edu.shop.exception.ExceptionMessage;
import pl.lodz.p.edu.shop.presentation.validation.annotation.Capitalized;
import pl.lodz.p.edu.shop.presentation.validation.annotation.PostalCode;

@Builder
public record AddressCreateDto(
    @PostalCode
    String postalCode,
    @Capitalized
    String country,
    @Capitalized
    String city,
    @Capitalized
    String street,
    @NotNull(message = ExceptionMessage.Validation.NOT_NULL)
    @Positive(message = ExceptionMessage.Validation.POSITIVE)
    Integer houseNumber
) {
}
