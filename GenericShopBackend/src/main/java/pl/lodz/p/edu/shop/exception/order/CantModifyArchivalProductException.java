package pl.lodz.p.edu.shop.exception.order;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class CantModifyArchivalProductException extends ResponseStatusException {
    public CantModifyArchivalProductException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
