package pl.lodz.p.edu.shop.logic.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.lodz.p.edu.shop.config.security.property.JwtProperties;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountRole;
import pl.lodz.p.edu.shop.exception.ApplicationExceptionFactory;
import pl.lodz.p.edu.shop.exception.SystemExceptionFactory;
import pl.lodz.p.edu.shop.logic.service.api.JwtService;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.time.Instant.now;


@Slf4j

@Service
class JwtServiceImpl implements JwtService {

    private final JwtProperties authTokenProperties;
    private final JwtProperties refreshTokenProperties;
    private final JwtProperties verificationTokenProperties;
    private final JwtProperties resetPasswordProperties;
    private final ObjectMapper objectMapper;

    public JwtServiceImpl(
        @Qualifier("authTokenProperties") JwtProperties authTokenProperties,
        @Qualifier("refreshTokenProperties") JwtProperties refreshTokenProperties,
        @Qualifier("verificationTokenProperties") JwtProperties verificationTokenProperties,
        @Qualifier("resetPasswordTokenProperties") JwtProperties resetPasswordProperties,
        ObjectMapper objectMapper
    ) {
        this.authTokenProperties = authTokenProperties;
        this.refreshTokenProperties = refreshTokenProperties;
        this.verificationTokenProperties = verificationTokenProperties;
        this.resetPasswordProperties = resetPasswordProperties;
        this.objectMapper = objectMapper;
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
    public String generateVerificationToken(String subject, String email) {

        Date expiration = Date.from(now().plusMillis(verificationTokenProperties.getTimeoutInMillis()));
        Key key = getSigningKeyFromNotEncodedSecret(email);

        return generateTokenWithIssuedAtNow(subject, Map.of(), expiration, key, SignatureAlgorithm.HS256);
    }

    @Override
    public String generateResetPasswordToken(String subject, String password) {

        Date expiration = Date.from(now().plusMillis(resetPasswordProperties.getTimeoutInMillis()));
        Key key = getSigningKeyFromNotEncodedSecret(password);

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

    @Override
    public void validateVerificationToken(String verificationToken, String email) {
        Key key = getSigningKeyFromNotEncodedSecret(email);

        validateAndExtractClaimsFromJwtToken(verificationToken, key);
    }

    @Override
    public void validateResetPasswordToken(String token, String password) {
        Key key = getSigningKeyFromNotEncodedSecret(password);

        validateAndExtractClaimsFromJwtToken(token, key);
    }

    @Override
    public String decodeSubjectFromJwtTokenWithoutValidation(String jwtToken) {
        try {
            String[] tokenParts = jwtToken.split("\\.");
            String decodedPayload = new String(Base64.getDecoder().decode(tokenParts[1]));
            JsonNode jsonNode = objectMapper.readTree(decodedPayload);
            return jsonNode.get("sub").asText();

        } catch (Exception e) {
            log.warn("Couldn't decode subject: ", e);
            throw SystemExceptionFactory.createDecodeException(e);
        }
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
            throw ApplicationExceptionFactory.createExpiredTokenException();

        } catch (JwtException e) {
            log.info("Jwt token invalid", e);
            throw ApplicationExceptionFactory.createInvalidTokenException();
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

        var rolesEntry = Map.entry("accountRoles", roles);
        var langEntry = Map.entry("lang", account.getLocale());

        return Map.ofEntries(rolesEntry, langEntry);
    }

    private Key getSigningKeyFromBase64EncodedSecret(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Key getSigningKeyFromNotEncodedSecret(String secret) {
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-256");
            byte[] digest = instance.digest(secret.getBytes());
            String encodedDigest = Encoders.BASE64.encode(digest);

            return Keys.hmacShaKeyFor(encodedDigest.getBytes());
        } catch (NoSuchAlgorithmException e) {
            log.warn("No algorithm provided for message digest: ", e);
            throw new RuntimeException(e);
        }
    }

}
