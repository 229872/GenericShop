package pl.lodz.p.edu.presentation.dto.user.account;

import lombok.Builder;
import pl.lodz.p.edu.presentation.validation.annotation.Password;

@Builder
public record ChangePasswordDto(
    @Password
    String currentPassword,
    @Password
    String newPassword
) {

}
