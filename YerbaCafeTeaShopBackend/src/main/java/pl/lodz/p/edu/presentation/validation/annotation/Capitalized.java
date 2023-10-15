package pl.lodz.p.edu.presentation.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pl.lodz.p.edu.exception.ExceptionMessage;

import java.lang.annotation.*;

@Documented
@NotNull(message = ExceptionMessage.Validation.FIELD_NOT_NULL)
@Pattern(regexp = "^[\\p{Lu}][\\p{L}\\p{M}*\\s-\\d]*$",
    message = ExceptionMessage.Validation.FIELD_CAPITALIZED)
@Size(min = 1, max = 20, message = ExceptionMessage.Validation.FIELD_CAPITALIZED_SIZE)
@Constraint(validatedBy = {})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Capitalized {

    String message() default ExceptionMessage.Validation.FIELD_CAPITALIZED;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

