package pl.lodz.p.edu.shop.presentation.dto.user.account;

import pl.lodz.p.edu.shop.presentation.validation.annotation.Email;

public record ForgotPasswordDto(
    @Email
    String email
) {
}
