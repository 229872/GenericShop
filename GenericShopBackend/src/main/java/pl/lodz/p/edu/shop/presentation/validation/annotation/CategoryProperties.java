package pl.lodz.p.edu.shop.presentation.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pl.lodz.p.edu.shop.exception.ExceptionMessage;
import pl.lodz.p.edu.shop.presentation.validation.validator.CategoryPropertiesValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = {CategoryPropertiesValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface CategoryProperties {

    String message() default ExceptionMessage.Validation.CATEGORY_PROPERTIES_NOT_VALID;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}