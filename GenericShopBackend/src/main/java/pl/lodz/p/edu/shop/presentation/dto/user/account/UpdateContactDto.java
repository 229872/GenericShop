package pl.lodz.p.edu.shop.presentation.dto.user.account;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.experimental.Delegate;
import pl.lodz.p.edu.shop.presentation.dto.user.address.InputAddressDto;
import pl.lodz.p.edu.shop.presentation.validation.annotation.Capitalized;

@Builder
public record UpdateContactDto(
    @NotBlank
    String version,
    @Capitalized
    String firstName,
    @Capitalized
    String lastName,
    @Valid @NotNull @Delegate
    InputAddressDto address
) {
}
