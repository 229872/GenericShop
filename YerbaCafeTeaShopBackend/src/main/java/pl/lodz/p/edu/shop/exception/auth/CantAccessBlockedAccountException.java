package pl.lodz.p.edu.shop.exception.auth;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class CantAccessBlockedAccountException extends ResponseStatusException {
    public CantAccessBlockedAccountException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
