package pl.lodz.p.edu.shop.presentation.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pl.lodz.p.edu.shop.exception.ExceptionMessage;

import java.lang.annotation.*;

@Documented
@NotNull(message = ExceptionMessage.Validation.NOT_NULL)
@Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&+=]).*$",
    message = ExceptionMessage.Validation.PASSWORD_WRONG)
@Size(min = 8, max = 30, message = ExceptionMessage.Validation.PASSWORD_WRONG_SIZE)
@Constraint(validatedBy = {})
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {

    String message() default ExceptionMessage.Validation.PASSWORD_WRONG;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
