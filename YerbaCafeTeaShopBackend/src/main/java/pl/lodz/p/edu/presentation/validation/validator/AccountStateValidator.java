package pl.lodz.p.edu.presentation.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.lodz.p.edu.presentation.validation.annotation.AccountState;

import java.util.Arrays;

public class AccountStateValidator implements ConstraintValidator<AccountState, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Arrays.stream(pl.lodz.p.edu.dataaccess.model.enumerated.AccountState.values())
            .map(Enum::name)
            .anyMatch(state -> state.equalsIgnoreCase(value));
    }
}
