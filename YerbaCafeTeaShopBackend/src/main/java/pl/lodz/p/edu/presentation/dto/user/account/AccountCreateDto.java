package pl.lodz.p.edu.presentation.dto.user.account;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import pl.lodz.p.edu.presentation.dto.user.address.AddressCreateDto;
import pl.lodz.p.edu.presentation.validation.annotation.Email;

@Builder
public record AccountCreateDto(
    @NotBlank
    String login,
    @Email
    String email,
    @NotBlank
    String password,
    @NotBlank
    String locale,
    @NotBlank
    String firstName,
    @NotBlank
    String lastName,
    @Valid
    AddressCreateDto address,
    @NotBlank
    String accountState,
    @NotBlank
    String role
) {
}
