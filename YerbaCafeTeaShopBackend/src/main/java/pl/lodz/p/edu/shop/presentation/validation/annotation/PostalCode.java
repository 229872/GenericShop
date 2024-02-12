package pl.lodz.p.edu.shop.presentation.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import pl.lodz.p.edu.shop.exception.ExceptionMessage;

import java.lang.annotation.*;

@Documented
@NotNull(message = ExceptionMessage.Validation.NOT_NULL)
@Pattern(regexp = "^\\d{2}-\\d{3}$", message = ExceptionMessage.Validation.ACCOUNT_POSTAL_CODE_WRONG)
@Constraint(validatedBy = {})
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostalCode {

    String message() default ExceptionMessage.Validation.ACCOUNT_POSTAL_CODE_WRONG;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
