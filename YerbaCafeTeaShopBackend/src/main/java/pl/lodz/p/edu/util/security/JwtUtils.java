package pl.lodz.p.edu.util.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.lodz.p.edu.dataaccess.model.sub.AccountRole;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {

    @Value("${security.token.timeout_in_minutes:3}")
    private Long tokenTimeoutInMinutes;

    @Value("${security.token.key:3}")
    private String secretKey;

    public String generateToken(String login, Set<AccountRole> accountRoles) {
        long tokenTimeoutInMillis = TimeUnit.MINUTES.toMillis(tokenTimeoutInMinutes);
        long currentTimeMillis = System.currentTimeMillis();
        List<String> roles = accountRoles.stream()
            .map(AccountRole::name)
            .toList();

        return Jwts.builder()
            .setSubject(login)
            .setIssuedAt(new Date(currentTimeMillis))
            .setExpiration(new Date(currentTimeMillis + tokenTimeoutInMillis))
            .claim("roles", roles)
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact();
    }

    public Jws<Claims> parseJwt(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey.getBytes()).build().parseClaimsJws(token);
    }

}
