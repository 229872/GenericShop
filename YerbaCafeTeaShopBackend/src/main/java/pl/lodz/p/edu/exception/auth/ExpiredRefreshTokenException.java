package pl.lodz.p.edu.exception.auth;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class ExpiredRefreshTokenException extends ResponseStatusException {
    public ExpiredRefreshTokenException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
