package pl.lodz.p.edu.presentation.validation.validator;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.lodz.p.edu.presentation.validation.annotation.Locale;

import java.util.Arrays;
import java.util.List;

public class LocaleValidator implements ConstraintValidator<Locale, String> {

    private final List<String> supportedLocales = Arrays.asList("en", "pl");

    @Override
    public boolean isValid(String language, ConstraintValidatorContext context) {
        return supportedLocales.contains(language);
    }
}
