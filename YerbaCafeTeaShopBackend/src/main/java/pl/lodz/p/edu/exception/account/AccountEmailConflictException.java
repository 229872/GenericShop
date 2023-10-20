package pl.lodz.p.edu.exception.account;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class AccountEmailConflictException extends ResponseStatusException {
    public AccountEmailConflictException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
