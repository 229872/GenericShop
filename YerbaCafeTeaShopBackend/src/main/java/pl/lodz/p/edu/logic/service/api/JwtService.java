package pl.lodz.p.edu.logic.service.api;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import pl.lodz.p.edu.dataaccess.model.Account;

public interface JwtService {

    String generateToken(Account account);

    Jws<Claims> getTokenClaims(String token);

    String generateRefreshToken(Account account);

    void validateRefreshToken(String refreshToken);

    String getLoginFromRefreshToken(String refreshToken);
}
