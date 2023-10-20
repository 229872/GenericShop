package pl.lodz.p.edu.logic.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.lodz.p.edu.dataaccess.model.sub.AccountRole;
import pl.lodz.p.edu.logic.service.api.JwtService;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${security.token.timeout_in_minutes:3}")
    private Long tokenTimeoutInMinutes;

    @Value("${security.token.key:3}")
    private String secretKey;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
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
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }

    @Override
    public Jws<Claims> parseJwt(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token);
    }

}
