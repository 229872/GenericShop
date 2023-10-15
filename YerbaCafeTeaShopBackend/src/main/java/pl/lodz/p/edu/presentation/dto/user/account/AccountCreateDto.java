package pl.lodz.p.edu.presentation.dto.user.account;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import pl.lodz.p.edu.presentation.dto.user.address.AddressCreateDto;
import pl.lodz.p.edu.presentation.validation.annotation.*;

@Builder
public record AccountCreateDto(
    @Login
    String login,
    @Email
    String email,
    @Password
    String password,
    @Locale
    String locale,
    @Capitalized
    String firstName,
    @Capitalized
    String lastName,
    @NotNull
    @Valid
    AddressCreateDto address,
    @AccountState
    String accountState,
    @AccountRole
    String role
) {
}
