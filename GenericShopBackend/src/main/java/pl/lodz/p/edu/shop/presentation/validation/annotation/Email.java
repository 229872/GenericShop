package pl.lodz.p.edu.shop.presentation.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import pl.lodz.p.edu.shop.exception.ExceptionMessage;

import java.lang.annotation.*;

@Documented
@NotNull(message = ExceptionMessage.Validation.NOT_NULL)
@jakarta.validation.constraints.Email(message = ExceptionMessage.Validation.EMAIL)
@Constraint(validatedBy = {})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {

    String message() default ExceptionMessage.Validation.EMAIL;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
