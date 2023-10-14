package pl.lodz.p.edu.presentation.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.lodz.p.edu.presentation.validation.annotation.AccountRole;

import java.util.Arrays;

public class AccountRoleValidator implements ConstraintValidator<AccountRole, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Arrays.stream(pl.lodz.p.edu.dataaccess.model.sub.AccountRole.values())
            .map(Enum::name)
            .anyMatch(roleName -> roleName.equalsIgnoreCase(value));
    }
}
