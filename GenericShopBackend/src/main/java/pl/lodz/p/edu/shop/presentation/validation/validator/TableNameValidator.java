package pl.lodz.p.edu.shop.presentation.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.lodz.p.edu.shop.presentation.validation.annotation.TableName;

import java.util.*;

public class TableNameValidator implements ConstraintValidator<TableName, String> {

    private static final String TABLE_NAME_PATTERN = "^[a-zA-Z]{1,99}[a-zA-Z&&[^sS]]$";
    private static final Set<String> SQL_RESERVED_KEYWORDS = Set.of(
        "SELECT", "INSERT", "UPDATE", "DELETE", "FROM", "WHERE", "JOIN", "TABLE", "AND", "OR",
        "NOT", "NULL", "IN", "AS", "ON", "SET", "CREATE", "ALTER", "DROP", "TRUNCATE"
    );

    @Override
    public boolean isValid(String tableName, ConstraintValidatorContext context) {
        if (tableName == null || tableName.isEmpty()) {
            return false;
        }

        if (!tableName.matches(TABLE_NAME_PATTERN)) {
            return false;
        }

        return !SQL_RESERVED_KEYWORDS.contains(tableName.toUpperCase());
    }
}
