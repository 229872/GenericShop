package pl.lodz.p.edu.shop.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionMessage {

    public static final String UNKNOWN = "exception.unknown";
    public static final String DB_CONSTRAINT_VIOLATION = "exception.db.constraint.violation";
    public static final String DECODE_EXCEPTION = "exception.mapping";
    public static final String TRANSACTION_TIMEOUT = "exception.transaction.timeout";
    public static final String ACCOUNT_NOT_FOUND = "exception.account.not_found";
    public static final String ACCOUNT_CONFLICT_LOGIN = "exception.account.conflict.login";
    public static final String ACCOUNT_CONFLICT_EMAIL = "exception.account.conflict.newEmail";
    public static final String ACCOUNT_NOT_BLOCKED = "exception.account.state.not_blocked";
    public static final String ACCOUNT_NOT_ACTIVE = "exception.account.state.not_active";
    public static final String ACCOUNT_ARCHIVAL = "exception.account.state.archival";
    public static final String ACCOUNT_LAST_ROLE = "exception.account.role.last.role";
    public static final String ACCOUNT_ROLE_ALREADY_ASSIGNED = "exception.account.role.already.exists";
    public static final String ACCOUNT_ROLE_ADMIN_MANY_ROLES = "exception.account.role.admin.many.roles";
    public static final String ACCOUNT_ROLE_NOT_FOUND = "exception.account.role.not_found";
    public static final String ACCOUNT_ROLE_MORE_THAN_ONE = "exception.account.role.more.than.one.assigned";
    public static final String ACCOUNT_ROLE_CANT_ASSIGN_GUEST = "exception.account.role.cant.assign.guest";
    public static final String ACCOUNT_CREATE_MANY_ROLES = "exception.account.create.role.many";
    public static final String ACCOUNT_CREATE_CANT_ASSIGN_NOT_VERIFIED = "exception.account.create.not_verified";

    public static final String INVALID_CREDENTIALS = "exception.auth.credentials";
    public static final String AUTH_ACCOUNT_ARCHIVAL = "exception.auth.archival";
    public static final String AUTH_ACCOUNT_NOT_VERIFIED = "exception.auth.state.not_verified";
    public static final String AUTH_ACCOUNT_BLOCKED = "exception.auth.state.blocked";
    public static final String TOKEN_EXPIRED = "exception.auth.token.expired";
    public static final String TOKEN_INVALID = "exception.auth.token.invalid";


    public static class Validation {
        public static final String NOT_NULL = "exception.validation.field.not_null";
        public static final String BLANK = "exception.validation.field.blank";
        public static final String POSITIVE = "exception.validation.positive";
        public static final String CAPITALIZED = "exception.validation.field.capitalized";
        public static final String SIZE = "exception.validation.field..size";
        public static final String EMAIL = "exception.validation.newEmail.wrong";
        public static final String PASSWORD_WRONG_SIZE = "exception.validation.password.size";
        public static final String PASSWORD_WRONG = "exception.validation.password.wrong";
        public static final String ACCOUNT_LOCALE_NOT_SUPPORTED = "exception.validation.account.locale.not_supported";
        public static final String ACCOUNT_ROLE_NOT_SUPPORTED = "exception.validation.account.role.not_supported";
        public static final String ACCOUNT_STATE_NOT_SUPPORTED = "exception.validation.account.state.not_supported";
        public static final String ACCOUNT_POSTAL_CODE_WRONG = "exception.validation.account.address.postal_code.wrong";
        public static final String LOGIN_WRONG = "exception.validation.login.wrong";
    }
}
