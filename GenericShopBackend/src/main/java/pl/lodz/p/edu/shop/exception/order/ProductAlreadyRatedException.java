package pl.lodz.p.edu.shop.exception.order;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class ProductAlreadyRatedException extends ResponseStatusException {
    public ProductAlreadyRatedException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
