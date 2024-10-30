package pl.lodz.p.edu.shop.integration.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.edu.shop.AccountsModuleTestData;
import pl.lodz.p.edu.shop.config.PostgresqlContainerSetup;
import pl.lodz.p.edu.shop.config.security.property.JwtProperties;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountRole;
import pl.lodz.p.edu.shop.exception.ExceptionMessage;
import pl.lodz.p.edu.shop.exception.auth.ExpiredTokenException;
import pl.lodz.p.edu.shop.exception.auth.InvalidTokenException;
import pl.lodz.p.edu.shop.exception.other.DecodeException;
import pl.lodz.p.edu.shop.logic.service.api.JwtService;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

@DisplayName("Integration tests for jwtService")
@SpringBootTest
@ActiveProfiles("it")
public class JwtServiceImplIT extends PostgresqlContainerSetup {

    @Autowired
    private JwtService underTest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("authTokenProperties")
    private JwtProperties authTokenProperties;

    @Autowired
    @Qualifier("refreshTokenProperties")
    private JwtProperties refreshTokenProperties;

    @Autowired
    @Qualifier("verificationTokenProperties")
    private JwtProperties verificationTokenProperties;

    @Test
    @DisplayName("Should generate jwt auth token with login in subject, accountRoles in claims and duration provided in property")
    void generateAuthToken_positive_1() {
        //given
        Account givenAccount = AccountsModuleTestData.buildDefaultAccount();
        String givenLogin = givenAccount.getLogin();
        Set<AccountRole> givenAccountRoles = givenAccount.getAccountRoles();

        //when
        String result = underTest.generateAuthToken(givenAccount);

        //then
        String payload = decodeJwtToken(result).get(1);

        //Subject
        String sub = jsonNodeOf(payload).get("sub").asText();

        assertThat(sub)
            .isNotBlank()
            .isEqualTo(givenLogin);

        //Roles
        Set<AccountRole> resultRoles = new HashSet<>(givenAccountRoles.size());
        jsonNodeOf(payload).get("accountRoles").iterator().forEachRemaining(jsonNode -> {
            var accountRole = AccountRole.valueOf(jsonNode.asText());
            resultRoles.add(accountRole);
        });

        assertThat(resultRoles)
            .hasSize(givenAccountRoles.size())
            .containsAll(resultRoles);

        //Expiration
        long exp = jsonNodeOf(payload).get("exp").asLong();
        long iat = jsonNodeOf(payload).get("iat").asLong();

        long durationInSeconds = exp - iat;
        assertThat(durationInSeconds)
            .isPositive()
            .isEqualTo(TimeUnit.MINUTES.toSeconds(authTokenProperties.getTimeoutInMinutes()));
    }

    @Test
    @DisplayName("Should generate jwt refresh token with login in subject and duration provided in property")
    void generateRefreshToken_positive_1() {
        //given
        Account givenAccount = AccountsModuleTestData.buildDefaultAccount();
        String givenLogin = givenAccount.getLogin();

        //when
        String result = underTest.generateRefreshToken(givenLogin);

        //then
        String payload = decodeJwtToken(result).get(1);

        //Sub
        String sub = jsonNodeOf(payload).get("sub").asText();

        assertThat(sub)
            .isNotBlank()
            .isEqualTo(givenLogin);

        //Exp
        long exp = jsonNodeOf(payload).get("exp").asLong();
        long iat = jsonNodeOf(payload).get("iat").asLong();

        long durationInSeconds = exp - iat;

        assertThat(durationInSeconds)
            .isPositive()
            .isEqualTo(TimeUnit.MINUTES.toSeconds(refreshTokenProperties.getTimeoutInMinutes()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"e", "example@example.com", "verylargeemailaddressemailforaccount1234567892000324132@example.com"})
    @DisplayName("Should generate jwt verification token with login in subject and duration provided in property")
    void generateVerificationToken_positive_1(String givenEmail) {
        //given
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .email(givenEmail)
            .build();
        String givenLogin = givenAccount.getLogin();

        //when
        String result = underTest.generateVerificationToken(givenLogin, givenEmail);

        //then
        String payload = decodeJwtToken(result).get(1);

        //Sub
        String sub = jsonNodeOf(payload).get("sub").asText();

        assertThat(sub)
            .isNotBlank()
            .isEqualTo(givenLogin);

        //Exp
        long exp = jsonNodeOf(payload).get("exp").asLong();
        long iat = jsonNodeOf(payload).get("iat").asLong();

        long durationInSeconds = exp -iat;

        assertThat(durationInSeconds)
            .isPositive()
            .isEqualTo(TimeUnit.MINUTES.toSeconds(verificationTokenProperties.getTimeoutInMinutes()));

    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should validate and extract claims with login and accountRoles from valid auth token")
    void validateAndExtractClaimsFromAuthToken_positive_1() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT, AccountRole.EMPLOYEE));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();
        String givenLogin = givenAccount.getLogin();
        String givenAuthToken = underTest.generateAuthToken(givenAccount);

        //when
        Claims result = underTest.validateAndExtractClaimsFromAuthToken(givenAuthToken);

        //then
        assertThat(result.getSubject())
            .isEqualTo(givenLogin);

        List<String> resultRoles = result.get("accountRoles", List.class);
        Set<AccountRole> resultAccountRoles = resultRoles.stream()
            .map(AccountRole::valueOf)
            .collect(Collectors.toSet());

        assertThat(resultAccountRoles)
            .hasSize(givenRoles.size())
            .containsAll(givenRoles);
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when signature of auth token isn't valid")
    void validateAndExtractClaimsFromAuthToken_negative_1() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT, AccountRole.EMPLOYEE));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();
        String givenAuthToken = underTest.generateAuthToken(givenAccount);
        String[] splitToken = givenAuthToken.split("\\.");
        String wrongSignatureToken = splitToken[0].concat(splitToken[1]);

        //when
        Exception exception = catchException(() -> underTest.validateAndExtractClaimsFromAuthToken(wrongSignatureToken));

        //then
        assertThat(exception)
            .isNotNull()
            .isInstanceOf(ResponseStatusException.class)
            .isExactlyInstanceOf(InvalidTokenException.class)
            .hasMessageContaining(ExceptionMessage.TOKEN_INVALID);
    }

    @Test
    @DisplayName("Should throw ExpiredTokenException when auth token is expired")
    void validateAndExtractClaimsFromAuthToken_negative_2() {
        //given
        String givenSubject = AccountsModuleTestData.defaultLogin;
        HashMap<String, Object> givenClaims = new HashMap<>();
        Date givenIas = Date.from(Instant.now().minusMillis(TimeUnit.MINUTES.toMillis(60)));
        Date givenExpiration = Date.from(Instant.now().minusMillis(TimeUnit.MINUTES.toMillis(30)));
        Key givenKey = getSigningKeyFromBase64EncodedSecret(authTokenProperties.getKey());
        SignatureAlgorithm givenAlg = SignatureAlgorithm.HS256;

        String givenExpiredToken = generateToken(givenSubject, givenClaims, givenIas, givenExpiration, givenKey, givenAlg);

        //when
        Exception exception = catchException(() -> underTest.validateAndExtractClaimsFromAuthToken(givenExpiredToken));

        //then
        assertThat(exception)
            .isNotNull()
            .isInstanceOf(ResponseStatusException.class)
            .isInstanceOf(ExpiredTokenException.class)
            .hasMessageContaining(ExceptionMessage.TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when auth token was modified")
    void validateAndExtractClaimsFromAuthToken_negative_3() {
        //given
        String givenSubject = AccountsModuleTestData.defaultLogin;
        Date givenIas = Date.from(Instant.now());
        Date givenExpiration = Date.from(Instant.now().plusMillis(authTokenProperties.getTimeoutInMillis()));
        Key givenKey = getSigningKeyFromBase64EncodedSecret(authTokenProperties.getKey());
        SignatureAlgorithm givenAlg = SignatureAlgorithm.HS256;

        String givenAuthToken = generateToken(givenSubject, Map.of(), givenIas, givenExpiration, givenKey, givenAlg);

        //Try to attack application with changing subject of token to get access to another user
        String givenNewSubject = "anotherUserInApp";
        List<String> parts = new ArrayList<>(decodeJwtToken(givenAuthToken));
        String payload = parts.get(1);
        String modifiedPayload = payload.replace(givenSubject, givenNewSubject);
        parts.set(1, modifiedPayload);

        String givenAuthTokenWithReplacedSubject = encodeDecodedPartsOfToken(parts);

        //when
        Exception exception = catchException(() -> underTest.validateAndExtractClaimsFromAuthToken(givenAuthTokenWithReplacedSubject));

        //then
        assertThat(exception)
            .isNotNull()
            .isInstanceOf(ResponseStatusException.class)
            .isExactlyInstanceOf(InvalidTokenException.class)
            .hasMessageContaining(ExceptionMessage.TOKEN_INVALID);
    }

    @Test
    @DisplayName("Should validate and extract claims with login from valid refresh token")
    void validateAndExtractClaimsFromRefreshToken_positive_1() {
        //given
        String givenLogin = AccountsModuleTestData.defaultLogin;
        String givenRefreshToken = underTest.generateRefreshToken(givenLogin);

        //when
        Claims result = underTest.validateAndExtractClaimsFromRefreshToken(givenRefreshToken);

        //then
        assertThat(result.getSubject())
            .isEqualTo(givenLogin);
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when signature of refresh token isn't valid")
    void validateAndExtractClaimsFromRefreshToken_negative_1() {
        //given
        String givenLogin = AccountsModuleTestData.defaultLogin;
        String givenRefreshToken = underTest.generateRefreshToken(givenLogin);
        String[] splitToken = givenRefreshToken.split("\\.");
        String wrongSignatureToken = splitToken[0].concat(splitToken[1]);

        //when
        Exception exception = catchException(() -> underTest.validateAndExtractClaimsFromRefreshToken(wrongSignatureToken));

        //then
        assertThat(exception)
            .isNotNull()
            .isInstanceOf(ResponseStatusException.class)
            .isExactlyInstanceOf(InvalidTokenException.class)
            .hasMessageContaining(ExceptionMessage.TOKEN_INVALID);
    }

    @Test
    @DisplayName("Should throw ExpiredTokenException when refresh token is expired")
    void validateAndExtractClaimsFromRefreshToken_negative_2() {
        //given
        String givenSubject = AccountsModuleTestData.defaultLogin;
        HashMap<String, Object> givenClaims = new HashMap<>();
        Date givenIas = Date.from(Instant.now().minusMillis(TimeUnit.MINUTES.toMillis(60)));
        Date givenExpiration = Date.from(Instant.now().minusMillis(TimeUnit.MINUTES.toMillis(30)));
        Key givenKey = getSigningKeyFromBase64EncodedSecret(refreshTokenProperties.getKey());
        SignatureAlgorithm givenAlg = SignatureAlgorithm.HS256;

        String givenExpiredToken = generateToken(givenSubject, givenClaims, givenIas, givenExpiration, givenKey, givenAlg);

        //when
        Exception exception = catchException(() -> underTest.validateAndExtractClaimsFromRefreshToken(givenExpiredToken));

        //then
        assertThat(exception)
            .isNotNull()
            .isInstanceOf(ResponseStatusException.class)
            .isInstanceOf(ExpiredTokenException.class)
            .hasMessageContaining(ExceptionMessage.TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when refresh token was modified")
    void validateAndExtractClaimsFromRefreshToken_negative_3() {
        //given
        String givenSubject = AccountsModuleTestData.defaultLogin;
        Date givenIas = Date.from(Instant.now());
        Date givenExpiration = Date.from(Instant.now().plusMillis(refreshTokenProperties.getTimeoutInMillis()));
        Key givenKey = getSigningKeyFromBase64EncodedSecret(authTokenProperties.getKey());
        SignatureAlgorithm givenAlg = SignatureAlgorithm.HS256;

        String givenRefreshToken = generateToken(givenSubject, Map.of(), givenIas, givenExpiration, givenKey, givenAlg);

        //Try to attack application with changing subject of token to get access to another user
        String givenNewSubject = "anotherUserInApp";
        List<String> parts = new ArrayList<>(decodeJwtToken(givenRefreshToken));
        String payload = parts.get(1);
        String modifiedPayload = payload.replace(givenSubject, givenNewSubject);
        parts.set(1, modifiedPayload);

        String givenRefreshTokenWithReplacedSubject = encodeDecodedPartsOfToken(parts);

        //when
        Exception exception = catchException(() -> underTest.validateAndExtractClaimsFromRefreshToken(givenRefreshTokenWithReplacedSubject));

        //then
        assertThat(exception)
            .isNotNull()
            .isInstanceOf(ResponseStatusException.class)
            .isExactlyInstanceOf(InvalidTokenException.class)
            .hasMessageContaining(ExceptionMessage.TOKEN_INVALID);
    }

    @Test
    @DisplayName("Should validate and extract claims with login from valid verification token")
    void validateVerificationToken_positive_1() {
        //given
        Account givenAccount = AccountsModuleTestData.buildDefaultAccount();
        String givenLogin = givenAccount.getLogin();
        String givenEmail = givenAccount.getEmail();

        String givenValidVerificationToken = underTest.generateVerificationToken(givenLogin, givenEmail);

        //when
        Exception exception = catchException(() -> underTest.validateVerificationToken(givenValidVerificationToken, givenEmail));

        //then
        assertThat(exception)
            .isNull();
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when signature of verification token isn't valid")
    void validateVerificationToken_negative_1() {
        //given
        Account givenAccount = AccountsModuleTestData.buildDefaultAccount();
        String givenLogin = givenAccount.getLogin();
        String givenEmail = givenAccount.getEmail();
        String givenVerificationToken = underTest.generateVerificationToken(givenLogin, givenEmail);
        String[] splitToken = givenVerificationToken.split("\\.");
        String wrongSignatureToken = splitToken[0].concat(splitToken[1]);

        //when
        Exception exception = catchException(() -> underTest.validateVerificationToken(wrongSignatureToken, givenEmail));

        //then
        assertThat(exception)
            .isNotNull()
            .isInstanceOf(ResponseStatusException.class)
            .isExactlyInstanceOf(InvalidTokenException.class)
            .hasMessageContaining(ExceptionMessage.TOKEN_INVALID);
    }

    @Test
    @DisplayName("Should throw ExpiredTokenException when verification token is expired")
    void validateVerificationToken_negative_2() {
        //given
        String givenSubject = AccountsModuleTestData.defaultLogin;
        HashMap<String, Object> givenClaims = new HashMap<>();
        Date givenIas = Date.from(Instant.now().minusMillis(TimeUnit.MINUTES.toMillis(60)));
        Date givenExpiration = Date.from(Instant.now().minusMillis(TimeUnit.MINUTES.toMillis(30)));

        String givenEmail = AccountsModuleTestData.defaultEmail;
        Key givenKey = getSigningKeyFromNotEncodedSecret(givenEmail);
        SignatureAlgorithm givenAlg = SignatureAlgorithm.HS256;

        String givenExpiredToken = generateToken(givenSubject, givenClaims, givenIas, givenExpiration, givenKey, givenAlg);

        //when
        Exception exception = catchException(() -> underTest.validateVerificationToken(givenExpiredToken, givenEmail));

        //then
        assertThat(exception)
            .isNotNull()
            .isInstanceOf(ResponseStatusException.class)
            .isInstanceOf(ExpiredTokenException.class)
            .hasMessageContaining(ExceptionMessage.TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when verification token was modified")
    void validateVerificationToken_negative_3() {
        //given
        String givenSubject = AccountsModuleTestData.defaultLogin;
        String givenEmail = AccountsModuleTestData.defaultEmail;

        Date givenIas = Date.from(Instant.now());
        Date givenExpiration = Date.from(Instant.now().plusMillis(verificationTokenProperties.getTimeoutInMillis()));
        Key givenKey = getSigningKeyFromNotEncodedSecret(givenEmail);
        SignatureAlgorithm givenAlg = SignatureAlgorithm.HS256;

        String givenVerificationToken = generateToken(givenSubject, Map.of(), givenIas, givenExpiration, givenKey, givenAlg);

        //Try to attack application with changing subject of token to get access to another user
        String givenNewSubject = "anotherUserInApp";
        List<String> parts = new ArrayList<>(decodeJwtToken(givenVerificationToken));
        String payload = parts.get(1);
        String modifiedPayload = payload.replace(givenSubject, givenNewSubject);
        parts.set(1, modifiedPayload);

        String givenVerificationTokenWithReplacedSubject = encodeDecodedPartsOfToken(parts);

        //when
        Exception exception = catchException(() -> underTest.validateVerificationToken(givenVerificationTokenWithReplacedSubject, givenEmail));

        //then
        assertThat(exception)
            .isNotNull()
            .isInstanceOf(ResponseStatusException.class)
            .isExactlyInstanceOf(InvalidTokenException.class)
            .hasMessageContaining(ExceptionMessage.TOKEN_INVALID);
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when for validation is provided another newEmail")
    void validateVerificationToken_negative_4() {
        //given
        Account givenAccount = AccountsModuleTestData.buildDefaultAccount();
        String givenLogin = givenAccount.getLogin();
        String givenEmail = givenAccount.getEmail();
        String givenAnotherEmail = "example2@example.com";
        String givenVerificationToken = underTest.generateVerificationToken(givenLogin, givenEmail);

        //when
        Exception exception = catchException(() -> underTest.validateVerificationToken(givenVerificationToken, givenAnotherEmail));

        //then
        assertThat(exception)
            .isNotNull()
            .isInstanceOf(ResponseStatusException.class)
            .isExactlyInstanceOf(InvalidTokenException.class)
            .hasMessageContaining(ExceptionMessage.TOKEN_INVALID);
    }

    @ParameterizedTest
    @CsvFileSource(
        files = "src/test/resources/data/jwtTokens.csv",
        numLinesToSkip = 1
    )
    @DisplayName("Should decode subject from jwt token without validation")
    void decodeSubjectFromJwtTokenWithoutValidation_positive_1(String givenToken, String login) {
        //given

        //when
        String result = underTest.decodeSubjectFromJwtTokenWithoutValidation(givenToken);

        //then
        assertThat(result)
            .isNotNull()
            .isEqualTo(login);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "...", "a.b.c", "adfadfa"})
    @DisplayName("Should throw DecodeException when there is problem with decoding subject from token")
    void decodeSubjectFromJwtTokenWithoutValidation_positive_1(String givenToken) {
        //given

        //when
        Exception exception = catchException(() -> underTest.decodeSubjectFromJwtTokenWithoutValidation(givenToken));

        //then
        assertThat(exception)
            .isNotNull()
            .isInstanceOf(RuntimeException.class)
            .isExactlyInstanceOf(DecodeException.class)
            .hasMessageContaining(ExceptionMessage.DECODE_EXCEPTION);
    }


    private List<String> decodeJwtToken(String jwtToken) {
        String[] tokenParts = jwtToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        return Arrays.stream(tokenParts)
            .map(decoder::decode)
            .map(String::new)
            .toList();
    }

    private String encodeDecodedPartsOfToken(List<String> tokenParts) {
        if (tokenParts.size() != 3) {
            throw new IllegalArgumentException();
        }

        Base64.Encoder encoder = Base64.getUrlEncoder();
        return tokenParts.stream()
            .map(String::getBytes)
            .map(encoder::encodeToString)
            .collect(Collectors.joining("."));
    }

    private JsonNode jsonNodeOf(String json) throws RuntimeException {
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
            throw new RuntimeException(e);
        }
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
}
