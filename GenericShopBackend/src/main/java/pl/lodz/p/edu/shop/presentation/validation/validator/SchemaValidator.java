package pl.lodz.p.edu.shop.presentation.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ValidationException;
import pl.lodz.p.edu.shop.dataaccess.model.other.Constraint;
import pl.lodz.p.edu.shop.presentation.validation.annotation.Schema;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class SchemaValidator implements ConstraintValidator<Schema, Map<String, List<String>>> {

    @Override
    public boolean isValid(Map<String, List<String>> value, ConstraintValidatorContext context) {
        for (var entry : value.entrySet()) {
            try {
                checkIfAllValuesAllValidConstrains(entry.getValue());

                List<Constraint> constraints = entry.getValue().stream()
                    .map(Constraint::valueOf)
                    .toList();

                checkIfListHasOneColumnDataType(constraints);
                checkIfFirstElementIsColumnDataType(constraints);
                checkIfThereAreOnlyUniqueConstraints(constraints);

            } catch (ValidationException e) {
                return false;
            }
        }

        return true;
    }


    private void checkIfListHasOneColumnDataType(List<Constraint> constraints) {
        long count = constraints.stream()
            .filter(Constraint::isType)
            .count();

        if (count != 1) throw new ValidationException();
    }

    private void checkIfFirstElementIsColumnDataType(List<Constraint> constraints) {
        if (!constraints.get(0).isType()) throw new ValidationException();
    }

    private void checkIfThereAreOnlyUniqueConstraints(List<Constraint> constraints) {
        var set = new HashSet<>(constraints);

        if (set.size() != constraints.size()) throw new ValidationException();
    }

    private void checkIfAllValuesAllValidConstrains(List<String> constraints) {
        try {
            List<Constraint> list = constraints.stream()
                .map(Constraint::valueOf)
                .toList();


        } catch (IllegalArgumentException e) {
            throw new ValidationException();
        }
    }
}
