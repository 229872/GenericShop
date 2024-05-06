package pl.lodz.p.edu.shop.logic.service.api;

import pl.lodz.p.edu.shop.logic.model.JwtTokens;

public interface AuthenticationService {

    JwtTokens authenticate(String login, String password);

    JwtTokens extendSession(String login, String refreshToken);
}
