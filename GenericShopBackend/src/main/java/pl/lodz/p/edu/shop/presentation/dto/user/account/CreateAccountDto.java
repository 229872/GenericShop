package pl.lodz.p.edu.shop.presentation.dto.user.account;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import pl.lodz.p.edu.shop.presentation.dto.user.address.InputAddressDto;
import pl.lodz.p.edu.shop.presentation.validation.annotation.*;

@Builder
public record CreateAccountDto(
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
    @NotNull @Valid
    InputAddressDto address,
    @AccountState
    String accountState,
    @AccountRole
    String role
) {
}
