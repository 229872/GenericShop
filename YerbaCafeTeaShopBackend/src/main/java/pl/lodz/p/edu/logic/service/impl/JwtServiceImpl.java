package pl.lodz.p.edu.logic.service.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.lodz.p.edu.dataaccess.model.Account;
import pl.lodz.p.edu.dataaccess.model.sub.AccountRole;
import pl.lodz.p.edu.exception.ExceptionFactory;
import pl.lodz.p.edu.logic.service.api.JwtService;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${security.token.timeout_in_minutes:3}")
    private Long tokenTimeoutInMinutes;

    @Value("${security.token.key:3}")
    private String tokenSecretKey;

    @Value("${security.refresh_token.timeout_in_minutes:20}")
    private Long refreshTokenTimeoutInMinutes;

    @Value("${security.refresh_token.key:3}")
    private String refreshTokenSecretKey;



    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateToken(Account account) {
        long tokenTimeoutInMillis = TimeUnit.MINUTES.toMillis(tokenTimeoutInMinutes);
        long currentTimeMillis = System.currentTimeMillis();
        List<String> roles = account.getAccountRoles().stream()
            .map(AccountRole::name)
            .toList();

        return Jwts.builder()
            .setSubject(account.getLogin())
            .setIssuedAt(new Date(currentTimeMillis))
            .setExpiration(new Date(currentTimeMillis + tokenTimeoutInMillis))
            .claim("roles", roles)
            .claim("lang", account.getLocale())
            .signWith(getSigningKey(tokenSecretKey), SignatureAlgorithm.HS512)
            .compact();
    }

    @Override
    public Jws<Claims> getTokenClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey(tokenSecretKey))
            .build()
            .parseClaimsJws(token);
    }

    @Override
    public String generateRefreshToken(Account account) {
        long tokenTimeoutInMillis = TimeUnit.MINUTES.toMillis(refreshTokenTimeoutInMinutes);
        long currentTimeMillis = System.currentTimeMillis();

        return Jwts.builder()
            .setSubject(account.getLogin())
            .setIssuedAt(new Date(currentTimeMillis))
            .setExpiration(new Date(currentTimeMillis + tokenTimeoutInMillis))
            .signWith(getSigningKey(refreshTokenSecretKey), SignatureAlgorithm.HS512)
            .compact();
    }

    @Override
    public void validateRefreshToken(String refreshToken) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(getSigningKey(refreshTokenSecretKey))
                .build();
            parser.parseClaimsJws(refreshToken);

        } catch (ExpiredJwtException e) {
            throw ExceptionFactory.createExpiredRefreshTokenException();
        } catch (RuntimeException e) {
            throw ExceptionFactory.createInvalidRefreshTokenException();
        }
    }

    @Override
    public String getLoginFromRefreshToken(String refreshToken) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(getSigningKey(refreshTokenSecretKey))
                .build();
            Claims claims = parser.parseClaimsJws(refreshToken).getBody();

            return claims.getSubject();
        } catch (RuntimeException e) {
            throw ExceptionFactory.createInvalidRefreshTokenException();
        }
    }

}
