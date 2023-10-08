package pl.lodz.p.edu.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;
import static pl.lodz.p.edu.exception.ExceptionMessage.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionFactory {

    public static ResponseStatusException createUnknownException() {
        return new UnknownException(INTERNAL_SERVER_ERROR, UNKNOWN);
    }

    public static ResponseStatusException createTransactionTimeoutException() {
        return new TransactionTimeoutException(GATEWAY_TIMEOUT, TRANSACTION_TIMEOUT);
    }

     public static ResponseStatusException createAccountNotFoundException() {
        return new AccountNotFoundException(NOT_FOUND, ACCOUNT_NOT_FOUND);
    }

    public static ResponseStatusException createAccountLoginConflictException() {
        return new AccountLoginConflictException(CONFLICT, ACCOUNT_CONFLICT_LOGIN);
    }

    public static ResponseStatusException createAccountEmailConflictException() {
        return new AccountEmailConflictException(CONFLICT, ACCOUNT_CONFLICT_EMAIL);
    }

    public static ResponseStatusException createAccountNotBlockedException() {
        return new AccountNotBlockedException(BAD_REQUEST, ACCOUNT_NOT_BLOCKED);
    }

    public static ResponseStatusException createAccountNotActiveException() {
        return new AccountNotActiveException(BAD_REQUEST, ACCOUNT_NOT_ACTIVE);
    }

    public static ResponseStatusException createAccountAlreadyArchivalException() {
        return new AccountAlreadyArchivalException(BAD_REQUEST, ACCOUNT_ALREADY_ARCHIVAL);
    }

    public static ResponseStatusException createAccountCantRemoveLastRoleException() {
        return new AccountCantRemoveLastRoleException(BAD_REQUEST, ACCOUNT_LAST_ROLE);
    }

    public static ResponseStatusException createAccountRoleAlreadyAssignedException() {
        return new AccountRoleAlreadyAssigned(BAD_REQUEST, ACCOUNT_ROLE_ALREADY_EXISTS);
    }

    public static ResponseStatusException createAccountRoleConflictException() {
        return new AccountRoleConflictException(CONFLICT, ACCOUNT_ROLE_CONFLICT);
    }

    public static ResponseStatusException createAccountRoleNotFoundException() {
        return new AccountRoleNotFoundException(BAD_REQUEST, ACCOUNT_ROLE_NOT_FOUND);
    }
}
