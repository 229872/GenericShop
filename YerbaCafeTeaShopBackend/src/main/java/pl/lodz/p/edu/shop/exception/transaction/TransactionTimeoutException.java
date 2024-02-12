package pl.lodz.p.edu.shop.exception.transaction;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class TransactionTimeoutException extends ResponseStatusException {
    public TransactionTimeoutException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
