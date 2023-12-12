package pl.lodz.p.edu.exception.transaction;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class UnknownConflictException extends ResponseStatusException {
    public UnknownConflictException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
