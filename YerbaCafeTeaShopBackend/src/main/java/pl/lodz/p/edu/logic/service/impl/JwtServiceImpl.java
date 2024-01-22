package pl.lodz.p.edu.logic.service.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.lodz.p.edu.config.database.property.JwtProperties;
import pl.lodz.p.edu.dataaccess.model.entity.Account;
import pl.lodz.p.edu.dataaccess.model.enumerated.AccountRole;
import pl.lodz.p.edu.exception.ApplicationExceptionFactory;
import pl.lodz.p.edu.logic.service.api.JwtService;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
class JwtServiceImpl implements JwtService {

    private final JwtProperties tokenProperties;
    private final JwtProperties refreshTokenProperties;

    public JwtServiceImpl(
        @Qualifier("tokenProperties") JwtProperties tokenProperties,
        @Qualifier("refreshTokenProperties") JwtProperties refreshTokenProperties) {
        this.tokenProperties = tokenProperties;
        this.refreshTokenProperties = refreshTokenProperties;
    }

    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateToken(Account account) {
        long tokenTimeoutInMillis = tokenProperties.getTimeoutInMillis();
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
            .signWith(getSigningKey(tokenProperties.getKey()), SignatureAlgorithm.HS512)
            .compact();
    }

    @Override
    public Jws<Claims> getTokenClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey(tokenProperties.getKey()))
            .build()
            .parseClaimsJws(token);
    }

    @Override
    public String generateRefreshToken(Account account) {
        long tokenTimeoutInMillis = refreshTokenProperties.getTimeoutInMillis();
        long currentTimeMillis = System.currentTimeMillis();

        return Jwts.builder()
            .setSubject(account.getLogin())
            .setIssuedAt(new Date(currentTimeMillis))
            .setExpiration(new Date(currentTimeMillis + tokenTimeoutInMillis))
            .signWith(getSigningKey(refreshTokenProperties.getKey()), SignatureAlgorithm.HS512)
            .compact();
    }

    @Override
    public void validateRefreshToken(String refreshToken) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(getSigningKey(refreshTokenProperties.getKey()))
                .build();
            parser.parseClaimsJws(refreshToken);

        } catch (ExpiredJwtException e) {
            throw ApplicationExceptionFactory.createExpiredRefreshTokenException();
        } catch (RuntimeException e) {
            throw ApplicationExceptionFactory.createInvalidRefreshTokenException();
        }
    }

    @Override
    public String getLoginFromRefreshToken(String refreshToken) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(getSigningKey(refreshTokenProperties.getKey()))
                .build();
            Claims claims = parser.parseClaimsJws(refreshToken).getBody();

            return claims.getSubject();
        } catch (RuntimeException e) {
            throw ApplicationExceptionFactory.createInvalidRefreshTokenException();
        }
    }

}
