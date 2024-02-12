package pl.lodz.p.edu.shop.exception.auth;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class InvalidRefreshTokenException extends ResponseStatusException {
    public InvalidRefreshTokenException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
