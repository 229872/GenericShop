package pl.lodz.p.edu.exception.account;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class AccountRoleAlreadyAssignedException extends ResponseStatusException {
    public AccountRoleAlreadyAssignedException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
