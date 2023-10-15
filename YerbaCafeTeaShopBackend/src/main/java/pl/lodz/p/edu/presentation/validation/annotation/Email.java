package pl.lodz.p.edu.presentation.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import pl.lodz.p.edu.exception.ExceptionMessage;

import java.lang.annotation.*;

@Documented
@NotNull(message = ExceptionMessage.Validation.FIELD_NOT_NULL)
@jakarta.validation.constraints.Email(message = ExceptionMessage.Validation.EMAIL_WRONG)
@Constraint(validatedBy = {})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {

    String message() default ExceptionMessage.Validation.EMAIL_WRONG;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
