package pl.lodz.p.edu.shop.exception.other;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class UnknownException extends ResponseStatusException {
    public UnknownException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
