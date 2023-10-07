package pl.lodz.p.edu.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class AccountNotActiveException extends ResponseStatusException {
    AccountNotActiveException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
