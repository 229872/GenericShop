package pl.lodz.p.edu.shop.presentation.dto.user.account;

import jakarta.validation.constraints.NotBlank;
import pl.lodz.p.edu.shop.presentation.validation.annotation.Password;

public record ResetPasswordDto(
    @Password
    String password,
    @NotBlank
    String resetPasswordToken
) {
}
