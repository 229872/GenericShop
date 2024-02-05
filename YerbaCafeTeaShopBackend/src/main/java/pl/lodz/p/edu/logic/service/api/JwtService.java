package pl.lodz.p.edu.logic.service.api;

import io.jsonwebtoken.Claims;
import pl.lodz.p.edu.dataaccess.model.entity.Account;

public interface JwtService {

    String generateAuthToken(Account account);

    String generateRefreshToken(String subject);

    Claims validateAndExtractClaimsFromAuthToken(String authToken);

    Claims validateAndExtractClaimsFromRefreshToken(String refreshToken);
}
