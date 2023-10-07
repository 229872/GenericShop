package pl.lodz.p.edu.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class AccountNotBlockedException extends ResponseStatusException {
    AccountNotBlockedException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
