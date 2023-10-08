package pl.lodz.p.edu.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class AccountRoleNotFoundException extends ResponseStatusException {
    AccountRoleNotFoundException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
