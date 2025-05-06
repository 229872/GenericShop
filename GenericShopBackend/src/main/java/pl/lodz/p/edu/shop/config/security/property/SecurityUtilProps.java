package pl.lodz.p.edu.shop.config.security.property;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties("app.security.version")
@Data
@Validated
public class SecurityUtilProps {
    @NotBlank(message = "app.security.version.key is missing set this property to use application")
    private String key;
}
