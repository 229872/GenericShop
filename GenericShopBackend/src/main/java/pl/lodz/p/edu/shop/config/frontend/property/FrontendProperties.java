package pl.lodz.p.edu.shop.config.frontend.property;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties("app.frontend")
public class FrontendProperties {

    @NotBlank(message = "Frontend application link must be set for emails and cors configuration")
    private String frontendAppUrl;

    @NotBlank(message = "Frontend application account verification link must be set for emails")
    private String frontendAccountVerificationUrl;

    @NotBlank(message = "Frontend application account reset password link must be set for emails")
    private String frontendAccountResetPasswordUrl;
}
