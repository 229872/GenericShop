package pl.lodz.p.edu.shop.exception.order;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class CategoryConflictException extends ResponseStatusException {
    public CategoryConflictException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
