package pl.lodz.p.edu.shop.presentation.dto.authentication;

import pl.lodz.p.edu.shop.presentation.validation.annotation.Login;
import pl.lodz.p.edu.shop.presentation.validation.annotation.Password;

public record Credentials(
    @Login
    String login,
    @Password
    String password
) {
}
