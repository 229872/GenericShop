package pl.lodz.p.edu.presentation.dto.authentication;

import pl.lodz.p.edu.presentation.validation.annotation.Login;
import pl.lodz.p.edu.presentation.validation.annotation.Password;

public record Credentials(
    @Login
    String login,
    @Password
    String password
) {
}
