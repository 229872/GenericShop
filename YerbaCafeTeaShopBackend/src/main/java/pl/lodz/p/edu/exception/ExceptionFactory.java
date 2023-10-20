package pl.lodz.p.edu.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.edu.exception.account.*;
import pl.lodz.p.edu.exception.account.helper.AccountStateOperation;
import pl.lodz.p.edu.exception.auth.CantAccessArchivalAccountException;
import pl.lodz.p.edu.exception.auth.CantAccessBlockedAccountException;
import pl.lodz.p.edu.exception.auth.CantAccessNotVerifiedAccountException;
import pl.lodz.p.edu.exception.auth.InvalidCredentialsException;
import pl.lodz.p.edu.exception.other.UnknownException;
import pl.lodz.p.edu.exception.transaction.TransactionTimeoutException;

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

    public static ResponseStatusException createOperationNotAllowedWithActualAccountStateException(AccountStateOperation operation) {
        String message = switch (operation) {
            case BLOCK -> ACCOUNT_NOT_ACTIVE;
            case UNBLOCK -> ACCOUNT_NOT_BLOCKED;
        };
        return new OperationNotAllowedWithActualAccountStateException(BAD_REQUEST, message);
    }

    public static ResponseStatusException createCantModifyArchivalAccountException() {
        return new CantModifyArchivalAccountException(BAD_REQUEST, ACCOUNT_ARCHIVAL);
    }

    public static ResponseStatusException createCantRemoveLastRoleException() {
        return new CantRemoveLastRoleException(BAD_REQUEST, ACCOUNT_LAST_ROLE);
    }

    public static ResponseStatusException createAccountRoleAlreadyAssignedException() {
        return new AccountRoleAlreadyAssignedException(BAD_REQUEST, ACCOUNT_ROLE_ALREADY_ASSIGNED);
    }

    public static ResponseStatusException createAccountWithAdministratorRoleCantHaveMoreRolesException() {
        return new AccountWithAdministratorRoleCantHaveMoreRolesException(BAD_REQUEST, ACCOUNT_ROLE_ADMIN_MANY_ROLES);
    }

    public static ResponseStatusException createAccountRoleNotFoundException() {
        return new AccountRoleNotFoundException(BAD_REQUEST, ACCOUNT_ROLE_NOT_FOUND);
    }

    public static ResponseStatusException createCantChangeRoleIfMoreThanOneAlreadyAssignedException() {
        return new CantChangeRoleIfMoreThanOneAlreadyAssignedException(BAD_REQUEST, ACCOUNT_ROLE_MORE_THAN_ONE);
    }

    public static ResponseStatusException createCantAssignGuestRoleException() {
        return new CantAssignGuestRoleException(BAD_REQUEST, ACCOUNT_ROLE_CANT_ASSIGN_GUEST);
    }

    public static ResponseStatusException createCantCreateAccountWithManyRolesException() {
        return new CantCreateAccountWithManyRolesException(BAD_REQUEST, ACCOUNT_CREATE_MANY_ROLES);
    }

    public static ResponseStatusException createCantCreateAccountWithNotVerifiedStatusException() {
        return new CantCreateAccountWithNotVerifiedStatusException(BAD_REQUEST, ACCOUNT_CREATE_CANT_ASSIGN_NOT_VERIFIED);
    }


    public static ResponseStatusException createInvalidCredentialsException() {
        return new InvalidCredentialsException(UNAUTHORIZED, INVALID_CREDENTIALS);
    }

    public static ResponseStatusException createCantAccessArchivalAccountException() {
        return new CantAccessArchivalAccountException(UNAUTHORIZED, AUTH_ACCOUNT_ARCHIVAL);
    }

    public static ResponseStatusException createCantAccessBlockedAccountException() {
        return new CantAccessBlockedAccountException(UNAUTHORIZED, AUTH_ACCOUNT_BLOCKED);
    }

    public static ResponseStatusException createCantAccessNotVerifiedAccountException() {
        return new CantAccessNotVerifiedAccountException(UNAUTHORIZED, AUTH_ACCOUNT_NOT_VERIFIED);
    }
}
