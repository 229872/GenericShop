package pl.lodz.p.edu.exception.auth;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class InvalidCredentialsException extends ResponseStatusException {
    public InvalidCredentialsException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
