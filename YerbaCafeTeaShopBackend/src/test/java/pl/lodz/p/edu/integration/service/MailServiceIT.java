package pl.lodz.p.edu.integration.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import pl.lodz.p.edu.config.PostgresqlContainerSetup;
import pl.lodz.p.edu.logic.service.api.MailService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

@DisplayName("Integration tests for MailService")
@SpringBootTest
@ActiveProfiles("it")
public class MailServiceIT extends PostgresqlContainerSetup {

    @Autowired
    private MailService mailService;

    @Autowired
    private Environment environment;

    @EnabledIf(value = "mailIsSet")
    @Test
    void sendSimpleMessage() {
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

    @Test
    void sendHtmlMessage() {
    }

    private boolean mailIsSet() {
        boolean isUsernameSet = Optional.ofNullable(environment.getProperty("spring.mail.username"))
            .filter(value -> !value.isBlank())
            .isPresent();

        boolean isPasswordSet = Optional.ofNullable(environment.getProperty("spring.mail.password"))
            .filter(value -> !value.isBlank())
            .isPresent();

        return isUsernameSet && isPasswordSet;
    }
}