package pl.lodz.p.edu.logic.service.api;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import pl.lodz.p.edu.dataaccess.model.sub.AccountRole;

import java.util.Set;

public interface JwtService {

    String generateToken(String login, Set<AccountRole> accountRoles);

    Jws<Claims> parseJwt(String token);
}
