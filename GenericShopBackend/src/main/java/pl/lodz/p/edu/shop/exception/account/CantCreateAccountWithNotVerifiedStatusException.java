package pl.lodz.p.edu.shop.exception.account;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class CantCreateAccountWithNotVerifiedStatusException extends ResponseStatusException {
    public CantCreateAccountWithNotVerifiedStatusException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
