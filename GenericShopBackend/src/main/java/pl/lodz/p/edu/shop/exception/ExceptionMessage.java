package pl.lodz.p.edu.shop.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionMessage {

    public static final String UNKNOWN = "exception.unknown";
    public static final String DB_CONSTRAINT_VIOLATION = "exception.db.constraint.violation";
    public static final String DECODE_EXCEPTION = "exception.mapping";
    public static final String TRANSACTION_TIMEOUT = "exception.transaction.timeout";
    public static final String TRANSACTION_OPTIMISTIC_LOCK = "exception.transaction.optimistic_lock";
    public static final String ACCOUNT_NOT_FOUND = "exception.account.not_found";
    public static final String ACCOUNT_CONFLICT_LOGIN = "exception.account.conflict.login";
    public static final String ACCOUNT_CONFLICT_EMAIL = "exception.account.conflict.newEmail";
    public static final String ACCOUNT_NOT_BLOCKED = "exception.account.accountState.not_blocked";
    public static final String ACCOUNT_NOT_ACTIVE = "exception.account.accountState.not_active";
    public static final String ACCOUNT_ARCHIVAL = "exception.account.accountState.archival";
    public static final String ACCOUNT_LAST_ROLE = "exception.account.role.last.role";
    public static final String ACCOUNT_ROLE_ALREADY_ASSIGNED = "exception.account.role.already.exists";
    public static final String ACCOUNT_ROLE_ADMIN_MANY_ROLES = "exception.account.role.admin.many.accountRoles";
    public static final String ACCOUNT_ROLE_NOT_FOUND = "exception.account.role.not_found";
    public static final String ACCOUNT_ROLE_MORE_THAN_ONE = "exception.account.role.more.than.one.assigned";
    public static final String ACCOUNT_ROLE_CANT_ASSIGN_GUEST = "exception.account.role.cant.assign.guest";
    public static final String ACCOUNT_CREATE_MANY_ROLES = "exception.account.create.role.many";
    public static final String ACCOUNT_CREATE_CANT_ASSIGN_NOT_VERIFIED = "exception.account.create.not_verified";

    public static final String INVALID_CREDENTIALS = "exception.auth.credentials";
    public static final String AUTH_ACCOUNT_ARCHIVAL = "exception.auth.archival";
    public static final String AUTH_ACCOUNT_NOT_VERIFIED = "exception.auth.accountState.not_verified";
    public static final String AUTH_ACCOUNT_BLOCKED = "exception.auth.accountState.blocked";
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
        public static final String ACCOUNT_STATE_NOT_SUPPORTED = "exception.validation.account.accountState.not_supported";
        public static final String ACCOUNT_POSTAL_CODE_WRONG = "exception.validation.account.address.postal_code.wrong";
        public static final String LOGIN_WRONG = "exception.validation.login.wrong";
        public static final String SCHEMA_NOT_VALID = "exception.validation.product_schema.wrong";
        public static final String TABLE_NAME_NOT_VALID = "exception.validation.table_name.wrong";
        public static final String CATEGORY_PROPERTIES_NOT_VALID = "exception.validation.category.properties.wrong";
        public static final String LIST_PRODUCT_NOT_VALID = "exception.validation.product.list.not_valid";
    }

    public static class Orders {
        public static final String PRODUCT_NOT_FOUND = "exception.orders.product.not_found";
        public static final String PRODUCT_ARCHIVAL = "exception.orders.product.archival";
        public static final String SCHEMA_NOT_FOUND = "exception.orders.schema.not_found";
        public static final String CATEGORY_CONFLICT = "exception.orders.category.conflict";
        public static final String CATEGORY_NOT_FOUND = "exception.orders.category.not_found";
        public static final String ORDER_CANT_FINISH = "exception.orders.wrong";
        public static final String ORDER_NOT_FOUND = "exception.orders.not_found";
        public static final String ORDER_PRODUCT_ALREADY_RATED = "exception.orders.product_already_rated";
        public static final String RATE_NOT_FOUND = "exception.orders.rateValue.not_found";
    }
}
