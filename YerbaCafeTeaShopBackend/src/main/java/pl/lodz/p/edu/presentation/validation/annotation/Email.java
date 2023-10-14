package pl.lodz.p.edu.presentation.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import pl.lodz.p.edu.exception.ExceptionMessage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
