package pl.lodz.p.edu.shop.exception.account;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class CantCreateAccountWithManyRolesException extends ResponseStatusException {
    public CantCreateAccountWithManyRolesException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
