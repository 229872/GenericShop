package pl.lodz.p.edu.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;
import static pl.lodz.p.edu.exception.ExceptionMessage.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionFactory {

    public ResponseStatusException createUnknownException() {
        return new UnknownException(INTERNAL_SERVER_ERROR, UNKNOWN);
    }

    public ResponseStatusException createTransactionTimeoutException() {
        return new TransactionTimeoutException(GATEWAY_TIMEOUT, TRANSACTION_TIMEOUT);
    }

    public ResponseStatusException createAccountNotFoundException() {
        return new AccountNotFoundException(NOT_FOUND, ACCOUNT_NOT_FOUND);
    }

    public ResponseStatusException createAccountLoginConflictException() {
        return new AccountLoginConflictException(CONFLICT, ACCOUNT_CONFLICT_LOGIN);
    }

    public ResponseStatusException createAccountEmailConflictException() {
        return new AccountEmailConflictException(CONFLICT, ACCOUNT_CONFLICT_EMAIL);
    }

    public ResponseStatusException createAccountNotBlockedException() {
        return new AccountNotBlockedException(BAD_REQUEST, ACCOUNT_NOT_BLOCKED);
    }

    public ResponseStatusException createAccountNotActiveException() {
        return new AccountNotActiveException(BAD_REQUEST, ACCOUNT_NOT_ACTIVE);
    }

    public ResponseStatusException createAccountAlreadyArchivalException() {
        return new AccountAlreadyArchivalException(BAD_REQUEST, ACCOUNT_ALREADY_ARCHIVAL);
    }

    public ResponseStatusException createAccountCantRemoveLastRoleException() {
        return new AccountCantRemoveLastRoleException(BAD_REQUEST, ACCOUNT_LAST_ROLE);
    }

    public ResponseStatusException createAccountRoleAlreadyAssignedException() {
        return new AccountRoleAlreadyAssigned(BAD_REQUEST, ACCOUNT_ROLE_ALREADY_EXISTS);
    }

    public ResponseStatusException createAccountRoleConflictException() {
        return new AccountRoleConflictException(CONFLICT, ACCOUNT_ROLE_CONFLICT);
    }
}
