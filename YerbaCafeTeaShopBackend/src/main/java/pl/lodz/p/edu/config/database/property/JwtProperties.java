package pl.lodz.p.edu.config.database.property;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.concurrent.TimeUnit;

@Data
@Validated
public class JwtProperties {

    @NotNull @Positive
    private Integer timeoutInMinutes;

    @NotBlank
    private String key;

    public long getTimeoutInMillis() {
        return TimeUnit.MINUTES.toMillis(timeoutInMinutes);
    }
}
