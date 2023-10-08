package pl.lodz.p.edu.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class AccountEmailConflictException extends ResponseStatusException {
    AccountEmailConflictException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}