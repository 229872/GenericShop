package pl.lodz.p.edu.shop.exception.order;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class SchemaNotFoundException extends ResponseStatusException {
    public SchemaNotFoundException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}