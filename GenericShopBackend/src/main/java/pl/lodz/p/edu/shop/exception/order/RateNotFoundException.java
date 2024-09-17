package pl.lodz.p.edu.shop.exception.order;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class RateNotFoundException extends ResponseStatusException {
    public RateNotFoundException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
