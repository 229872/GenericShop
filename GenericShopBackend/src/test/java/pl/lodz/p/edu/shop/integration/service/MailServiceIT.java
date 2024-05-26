package pl.lodz.p.edu.shop.integration.service;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import pl.lodz.p.edu.shop.config.PostgresqlContainerSetup;
import pl.lodz.p.edu.shop.logic.service.api.MailService;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Integration tests for MailService")
@SpringBootTest
@ActiveProfiles("it")
public class MailServiceIT extends PostgresqlContainerSetup {

    @Autowired
    private MailService mailService;

    private Environment environment;

    @Autowired
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @EnabledIf(value = "isMailSet")
    @DisplayName("Should send simple text newEmail message")
    @Test
    void sendSimpleMessage_positive_1() {
        //given
        String givenRecipientEmail = environment.getProperty("spring.mail.username");
        String givenSubject = "Subject";
        String givenMessage = "Message";

        //when
        Exception exception = catchException(() -> mailService.sendSimpleMessage(givenRecipientEmail, givenSubject, givenMessage));

        //then
        assertThat(exception)
            .isNull();
    }

    @EnabledIf(value = "isMailSet")
    @DisplayName("Should send html newEmail message")
    @Test
    void sendHtmlMessage_positive_2() {
        //given
        String givenRecipientEmail = environment.getProperty("spring.mail.username");
        String givenSubject = "Subject";
        String givenMessage = "Message";
        Map<String, Object> variables = Map.of(
            "hello", "Hello",
            "companyName", "Company",
            "subtitle", "Subtitle",
            "name", givenRecipientEmail,
            "content", givenMessage,
            "url", "url",
            "urlText", "Click this link",
            "footer", "Thanks"
        );

        //when
        Exception exception = catchException(() -> mailService.sendHtmlMessage(givenRecipientEmail, givenSubject,
            "emailVerificationTemplate", variables));

        //then
        assertThat(exception)
            .isNull();
    }

    @ParameterizedTest
    @DisplayName("Should send html newEmail message with verification token")
    @ValueSource(strings = {"pl", "en"})
    void sendVerificationMail_positive_3(String locale) {
        Assumptions.assumeTrue(isMailSet(), "Mail properties are not set, so there is no need to test sending mails");

        //given
        String givenRecipientEmail = environment.getProperty("spring.mail.username");
        String givenToken = String.valueOf(UUID.randomUUID());

        //when
        Exception exception = catchException(() -> mailService.sendVerificationMail(givenRecipientEmail, locale, givenToken));

        //then
        assertThat(exception)
            .isNull();
    }

    private boolean isMailSet() {
        boolean isUsernameSet = Optional.ofNullable(environment.getProperty("spring.mail.username"))
            .filter(value -> !value.isBlank())
            .isPresent();

        boolean isPasswordSet = Optional.ofNullable(environment.getProperty("spring.mail.password"))
            .filter(value -> !value.isBlank())
            .isPresent();

        boolean shouldSendMail = Optional.ofNullable(environment.getProperty("spring.mail.enable"))
            .map(Boolean::parseBoolean)
            .orElse(false);

        return isUsernameSet && isPasswordSet && shouldSendMail;
    }
}