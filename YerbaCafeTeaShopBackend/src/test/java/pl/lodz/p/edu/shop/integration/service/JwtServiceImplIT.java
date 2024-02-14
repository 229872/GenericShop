package pl.lodz.p.edu.shop.integration.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.lodz.p.edu.shop.TestData;
import pl.lodz.p.edu.shop.config.PostgresqlContainerSetup;
import pl.lodz.p.edu.shop.config.database.property.JwtProperties;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountRole;
import pl.lodz.p.edu.shop.logic.service.api.JwtService;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

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
    @DisplayName("Should generate jwt auth token with login in subject, roles in claims and duration provided in property")
    void generateAuthToken_positive_1() {
        //given
        Account givenAccount = TestData.buildDefaultAccount();
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
        jsonNodeOf(payload).get("roles").iterator().forEachRemaining(jsonNode -> {
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
        Account givenAccount = TestData.buildDefaultAccount();
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
        Account givenAccount = TestData.getDefaultAccountBuilder()
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

    }

    @ParameterizedTest
    @CsvFileSource(
        files = "src/test/resources/data/jwtTokens.csv",
        numLinesToSkip = 1
    )
    @DisplayName("Should decode subject from jwt token without validation")
    void decodeSubjectFromJwtTokenWithoutValidation(String givenToken, String login) {
        //given

        //when
        String result = underTest.decodeSubjectFromJwtTokenWithoutValidation(givenToken);

        //then
        assertThat(result)
            .isNotNull()
            .isEqualTo(login);
    }

    private List<String> decodeJwtToken(String jwtToken) {
        String[] tokenParts = jwtToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        return Arrays.stream(tokenParts)
            .map(decoder::decode)
            .map(String::new)
            .toList();
    }

    private JsonNode jsonNodeOf(String json) throws RuntimeException {
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
