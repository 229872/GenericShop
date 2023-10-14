package pl.lodz.p.edu.presentation.dto.user.account;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import pl.lodz.p.edu.presentation.dto.user.address.AddressCreateDto;
import pl.lodz.p.edu.presentation.validation.annotation.AccountRole;
import pl.lodz.p.edu.presentation.validation.annotation.Email;
import pl.lodz.p.edu.presentation.validation.annotation.Locale;

@Builder
public record AccountCreateDto(
    @NotBlank
    String login,
    @Email
    String email,
    @NotBlank
    String password,
    @Locale
    String locale,
    @NotBlank
    String firstName,
    @NotBlank
    String lastName,
    @Valid
    AddressCreateDto address,
    @NotBlank
    String accountState,
    @AccountRole
    String role
) {
}
