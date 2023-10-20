package pl.lodz.p.edu.exception.account;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class AccountLoginConflictException extends ResponseStatusException {
    public AccountLoginConflictException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
