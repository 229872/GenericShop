package pl.lodz.p.edu.presentation.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import pl.lodz.p.edu.exception.ExceptionMessage;
import pl.lodz.p.edu.presentation.validation.validator.LocaleValidator;

import java.lang.annotation.*;

@Documented
@NotBlank(message = ExceptionMessage.Validation.ACCOUNT_LOCALE_BLANK)
@Constraint(validatedBy = LocaleValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Locale {
    String message() default ExceptionMessage.Validation.ACCOUNT_LOCALE_NOT_SUPPORTED;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

