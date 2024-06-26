package pl.lodz.p.edu.shop.presentation.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import pl.lodz.p.edu.shop.exception.ExceptionMessage;
import pl.lodz.p.edu.shop.presentation.validation.validator.AccountRoleValidator;

import java.lang.annotation.*;

@Documented
@NotBlank(message = ExceptionMessage.Validation.BLANK)
@Constraint(validatedBy = AccountRoleValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccountRole {

    String message() default ExceptionMessage.Validation.ACCOUNT_ROLE_NOT_SUPPORTED;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
