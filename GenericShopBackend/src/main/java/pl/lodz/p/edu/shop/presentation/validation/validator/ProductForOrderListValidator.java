package pl.lodz.p.edu.shop.presentation.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.lodz.p.edu.shop.presentation.dto.product.ProductRequest;
import pl.lodz.p.edu.shop.presentation.validation.annotation.ProductForOrderList;

import java.util.List;
import java.util.function.Predicate;

public class ProductForOrderListValidator implements ConstraintValidator<ProductForOrderList, List<ProductRequest>> {

    @Override
    public boolean isValid(List<ProductRequest> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        Predicate<ProductRequest> isProductRequestItemValid = productRequest ->
            productRequest.id() > 0 && productRequest.quantity() > 0;

        return value.stream()
            .allMatch(isProductRequestItemValid);
    }
}
