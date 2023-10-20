package pl.lodz.p.edu.exception.account;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class CantAssignGuestRoleException extends ResponseStatusException {
    public CantAssignGuestRoleException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}