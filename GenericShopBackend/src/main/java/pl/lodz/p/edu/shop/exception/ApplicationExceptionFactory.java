package pl.lodz.p.edu.shop.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.edu.shop.exception.account.*;
import pl.lodz.p.edu.shop.exception.account.helper.AccountStateOperation;
import pl.lodz.p.edu.shop.exception.auth.*;
import pl.lodz.p.edu.shop.exception.order.*;
import pl.lodz.p.edu.shop.exception.other.ApplicationOptimisticLockException;
import pl.lodz.p.edu.shop.exception.other.UnknownException;
import pl.lodz.p.edu.shop.exception.transaction.TransactionTimeoutException;

import static org.springframework.http.HttpStatus.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationExceptionFactory {

    public static ResponseStatusException createUnknownException() {
        return new UnknownException(INTERNAL_SERVER_ERROR, ExceptionMessage.UNKNOWN);
    }

    public static ResponseStatusException createTransactionTimeoutException() {
        return new TransactionTimeoutException(GATEWAY_TIMEOUT, ExceptionMessage.TRANSACTION_TIMEOUT);
    }

     public static ResponseStatusException createAccountNotFoundException() {
        return new AccountNotFoundException(NOT_FOUND, ExceptionMessage.ACCOUNT_NOT_FOUND);
    }

    public static ResponseStatusException createAccountLoginConflictException() {
        return new AccountLoginConflictException(CONFLICT, ExceptionMessage.ACCOUNT_CONFLICT_LOGIN);
    }

    public static ResponseStatusException createAccountEmailConflictException() {
        return new AccountEmailConflictException(CONFLICT, ExceptionMessage.ACCOUNT_CONFLICT_EMAIL);
    }

    public static ResponseStatusException createOperationNotAllowedWithActualAccountStateException(AccountStateOperation operation) {
        String message = switch (operation) {
            case BLOCK -> ExceptionMessage.ACCOUNT_NOT_ACTIVE;
            case UNBLOCK -> ExceptionMessage.ACCOUNT_NOT_BLOCKED;
        };
        return new OperationNotAllowedWithActualAccountStateException(BAD_REQUEST, message);
    }

    public static ResponseStatusException createCantModifyArchivalAccountException() {
        return new CantModifyArchivalAccountException(BAD_REQUEST, ExceptionMessage.ACCOUNT_ARCHIVAL);
    }

    public static ResponseStatusException createCantRemoveLastRoleException() {
        return new CantRemoveLastRoleException(BAD_REQUEST, ExceptionMessage.ACCOUNT_LAST_ROLE);
    }

    public static ResponseStatusException createAccountRoleAlreadyAssignedException() {
        return new AccountRoleAlreadyAssignedException(BAD_REQUEST, ExceptionMessage.ACCOUNT_ROLE_ALREADY_ASSIGNED);
    }

    public static ResponseStatusException createAccountWithAdministratorRoleCantHaveMoreRolesException() {
        return new AccountWithAdministratorRoleCantHaveMoreRolesException(BAD_REQUEST, ExceptionMessage.ACCOUNT_ROLE_ADMIN_MANY_ROLES);
    }

    public static ResponseStatusException createAccountRoleNotFoundException() {
        return new AccountRoleNotFoundException(BAD_REQUEST, ExceptionMessage.ACCOUNT_ROLE_NOT_FOUND);
    }

    public static ResponseStatusException createCantChangeRoleIfMoreThanOneAlreadyAssignedException() {
        return new CantChangeRoleIfMoreThanOneAlreadyAssignedException(BAD_REQUEST, ExceptionMessage.ACCOUNT_ROLE_MORE_THAN_ONE);
    }

    public static ResponseStatusException createCantAssignGuestRoleException() {
        return new CantAssignGuestRoleException(BAD_REQUEST, ExceptionMessage.ACCOUNT_ROLE_CANT_ASSIGN_GUEST);
    }

    public static ResponseStatusException createCantCreateAccountWithManyRolesException() {
        return new CantCreateAccountWithManyRolesException(BAD_REQUEST, ExceptionMessage.ACCOUNT_CREATE_MANY_ROLES);
    }

    public static ResponseStatusException createCantCreateAccountWithNotVerifiedStatusException() {
        return new CantCreateAccountWithNotVerifiedStatusException(BAD_REQUEST, ExceptionMessage.ACCOUNT_CREATE_CANT_ASSIGN_NOT_VERIFIED);
    }

    public static ResponseStatusException createInvalidCredentialsException() {
        return new InvalidCredentialsException(UNAUTHORIZED, ExceptionMessage.INVALID_CREDENTIALS);
    }

    public static ResponseStatusException createCantAccessArchivalAccountException() {
        return new CantAccessArchivalAccountException(UNAUTHORIZED, ExceptionMessage.AUTH_ACCOUNT_ARCHIVAL);
    }

    public static ResponseStatusException createCantAccessBlockedAccountException() {
        return new CantAccessBlockedAccountException(UNAUTHORIZED, ExceptionMessage.AUTH_ACCOUNT_BLOCKED);
    }

    public static ResponseStatusException createCantAccessNotVerifiedAccountException() {
        return new CantAccessNotVerifiedAccountException(UNAUTHORIZED, ExceptionMessage.AUTH_ACCOUNT_NOT_VERIFIED);
    }

    public static ResponseStatusException createExpiredTokenException() {
        return new ExpiredTokenException(UNAUTHORIZED, ExceptionMessage.TOKEN_EXPIRED);
    }

    public static ResponseStatusException createInvalidTokenException() {
        return new InvalidTokenException(UNAUTHORIZED, ExceptionMessage.TOKEN_INVALID);
    }

    public static ResponseStatusException createApplicationOptimisticLockException() {
        return new ApplicationOptimisticLockException(CONFLICT, ExceptionMessage.TRANSACTION_OPTIMISTIC_LOCK);
    }

    public static ResponseStatusException createProductNotFoundException() {
        return new ProductNotFoundException(NOT_FOUND, ExceptionMessage.Orders.PRODUCT_NOT_FOUND);
    }

    public static ResponseStatusException createSchemaNotFoundException() {
        return new SchemaNotFoundException(NOT_FOUND, ExceptionMessage.Orders.SCHEMA_NOT_FOUND);
    }

    public static ResponseStatusException createCategoryConflictException() {
        return new CategoryConflictException(CONFLICT, ExceptionMessage.Orders.CATEGORY_CONFLICT);
    }

    public static ResponseStatusException createCategoryNotFoundException() {
        return new CategoryNotFoundException(NOT_FOUND, ExceptionMessage.Orders.CATEGORY_NOT_FOUND);
    }

    public static ResponseStatusException createCantModifyArchivalProductException() {
        return new CantModifyArchivalProductException(BAD_REQUEST, ExceptionMessage.Orders.PRODUCT_ARCHIVAL);
    }

    public static ResponseStatusException createCantFinishOrderException() {
        return new CantFinishOrderException(CONFLICT, ExceptionMessage.Orders.ORDER_CANT_FINISH);
    }

    public static ResponseStatusException createOrderNotFoundException() {
        return new OrderNotFoundException(NOT_FOUND, ExceptionMessage.Orders.ORDER_NOT_FOUND);
    }

    public static ResponseStatusException createProductAlreadyRatedException() {
        return new ProductAlreadyRatedException(CONFLICT, ExceptionMessage.Orders.ORDER_PRODUCT_ALREADY_RATED);
    }

    public static ResponseStatusException createRateNotFoundException() {
        return new RateNotFoundException(NOT_FOUND, ExceptionMessage.Orders.RATE_NOT_FOUND);
    }
}
