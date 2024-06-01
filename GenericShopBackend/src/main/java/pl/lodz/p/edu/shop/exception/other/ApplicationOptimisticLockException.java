package pl.lodz.p.edu.shop.exception.other;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class ApplicationOptimisticLockException extends ResponseStatusException {

    public ApplicationOptimisticLockException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
