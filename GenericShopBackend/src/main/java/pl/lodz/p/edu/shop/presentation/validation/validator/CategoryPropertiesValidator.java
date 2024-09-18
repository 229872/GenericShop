package pl.lodz.p.edu.shop.presentation.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.lodz.p.edu.shop.presentation.validation.annotation.CategoryProperties;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.function.Predicate.not;

public class CategoryPropertiesValidator implements ConstraintValidator<CategoryProperties, Map<String, Object>> {
    private static final Set<String> SQL_RESERVED_KEYWORDS = Set.of(
        "SELECT", "INSERT", "UPDATE", "DELETE", "FROM", "WHERE", "JOIN", "TABLE", "AND", "OR",
        "NOT", "NULL", "IN", "AS", "ON", "SET", "CREATE", "ALTER", "DROP", "TRUNCATE"
    );

    @Override
    public boolean isValid(Map<String, Object> properties, ConstraintValidatorContext context) {
        Function<Object, String> mapStringToUppercase = v -> {
            if (v instanceof String value) {
                return value.toUpperCase();
            }
            return "";
        };

        return properties.values().stream()
            .map(mapStringToUppercase)
            .filter(String::isBlank)
            .anyMatch(not(SQL_RESERVED_KEYWORDS::contains));
    }
}
