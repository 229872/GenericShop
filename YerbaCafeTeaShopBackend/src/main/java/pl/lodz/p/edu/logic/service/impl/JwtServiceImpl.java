package pl.lodz.p.edu.logic.service.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Map;

import static java.time.Instant.now;


@Slf4j

@Service
class JwtServiceImpl implements JwtService {

    private final JwtProperties authTokenProperties;
    private final JwtProperties refreshTokenProperties;

    public JwtServiceImpl(
        @Qualifier("authTokenProperties") JwtProperties authTokenProperties,
        @Qualifier("refreshTokenProperties") JwtProperties refreshTokenProperties) {
        this.authTokenProperties = authTokenProperties;
        this.refreshTokenProperties = refreshTokenProperties;
    }

    @Override
    public String generateAuthToken(Account account) {
        
        String subject = account.getLogin();
        Map<String, Object> claims = prepareAuthTokenClaims(account);
        Date expiration = Date.from(now().plusMillis(authTokenProperties.getTimeoutInMillis()));
        Key key = getSigningKeyFromBase64EncodedSecret(authTokenProperties.getKey());

        return generateTokenWithIssuedAtNow(subject, claims, expiration, key, SignatureAlgorithm.HS256);
    }

    @Override
    public String generateRefreshToken(String subject) {

        Date expiration = Date.from(now().plusMillis(refreshTokenProperties.getTimeoutInMillis()));
        Key key = getSigningKeyFromBase64EncodedSecret(refreshTokenProperties.getKey());

        return generateTokenWithIssuedAtNow(subject, Map.of(), expiration, key, SignatureAlgorithm.HS256);
    }

    @Override
    public Claims validateAndExtractClaimsFromAuthToken(String authToken) {
        Key key = getSigningKeyFromBase64EncodedSecret(authTokenProperties.getKey());

        return validateAndExtractClaimsFromJwtToken(authToken, key);
    }

    @Override
    public Claims validateAndExtractClaimsFromRefreshToken(String refreshToken) {
        Key key = getSigningKeyFromBase64EncodedSecret(refreshTokenProperties.getKey());

        return validateAndExtractClaimsFromJwtToken(refreshToken, key);
    }





    private Claims validateAndExtractClaimsFromJwtToken(String token, Key key) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        } catch (ExpiredJwtException e) {
            log.info("Jwt token expired", e);
            throw ApplicationExceptionFactory.createExpiredRefreshTokenException();

        } catch (JwtException e) {
            log.info("Jwt token invalid", e);
            throw ApplicationExceptionFactory.createInvalidRefreshTokenException();
        }
    }

    private String generateTokenWithIssuedAtNow(String subject, Map<String, Object> claims, Date expiration, Key key,
                                                SignatureAlgorithm alg) {

        Date issuedAt = Date.from(now());
        return generateToken(subject, claims, issuedAt, expiration, key, alg);
    }

    private String generateToken(String subject, Map<String, Object> claims, Date issuedAt, Date expiration, Key key,
                                 SignatureAlgorithm alg) {
        return Jwts.builder()
            .setSubject(subject)
            .addClaims(claims)
            .setIssuedAt(issuedAt)
            .setExpiration(expiration)
            .signWith(key, alg)
            .compact();
    }

    private static Map<String, Object> prepareAuthTokenClaims(Account account) {
        List<String> roles = account.getAccountRoles().stream()
            .map(AccountRole::toString)
            .toList();

        var rolesEntry = Map.entry("roles", roles);
        var langEntry = Map.entry("lang", account.getLocale());

        return Map.ofEntries(rolesEntry, langEntry);
    }

    private Key getSigningKeyFromBase64EncodedSecret(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Key getSigningKeyFromNotEncodedSecret(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

}
