package pl.lodz.p.edu.shop.presentation.dto.user.account;

import lombok.Builder;
import pl.lodz.p.edu.shop.presentation.validation.annotation.Password;

@Builder
public record ChangePasswordDto(
    @Password
    String currentPassword,
    @Password
    String newPassword
) {

}
