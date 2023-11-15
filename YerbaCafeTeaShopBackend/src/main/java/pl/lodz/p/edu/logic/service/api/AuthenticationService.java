package pl.lodz.p.edu.logic.service.api;

import pl.lodz.p.edu.logic.model.JwtTokens;

public interface AuthenticationService {

    JwtTokens authenticate(String login, String password);

    JwtTokens getAuthenticationToken(String login, String refreshToken);
}
