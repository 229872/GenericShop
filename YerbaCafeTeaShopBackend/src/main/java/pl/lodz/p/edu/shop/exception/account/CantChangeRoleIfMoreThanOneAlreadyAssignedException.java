package pl.lodz.p.edu.shop.exception.account;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class CantChangeRoleIfMoreThanOneAlreadyAssignedException extends ResponseStatusException {
    public CantChangeRoleIfMoreThanOneAlreadyAssignedException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
