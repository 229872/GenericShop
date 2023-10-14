package pl.lodz.p.edu.presentation.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import pl.lodz.p.edu.exception.ExceptionMessage;

import java.lang.annotation.*;

@Documented
@jakarta.validation.constraints.Email(message = ExceptionMessage.Validation.ACCOUNT_EMAIL_WRONG)
@NotBlank(message = ExceptionMessage.Validation.ACCOUNT_EMAIL_BLANK)
@Constraint(validatedBy = {})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {
    String message() default ExceptionMessage.Validation.ACCOUNT_EMAIL_WRONG;

    Class<?>[] group() default {};

    Class<? extends Payload>[] payload() default {};
}
