package pl.lodz.p.edu.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class CantModifyArchivalAccountException extends ResponseStatusException {
    CantModifyArchivalAccountException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
