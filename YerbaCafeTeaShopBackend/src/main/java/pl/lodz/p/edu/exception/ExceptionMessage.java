package pl.lodz.p.edu.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionMessage {

    public static final String UNKNOWN = "exception.unknown";
    public static final String TRANSACTION_TIMEOUT = "exception.transaction.timeout";
    public static final String ACCOUNT_NOT_FOUND = "exception.account.not.found";
    public static final String ACCOUNT_CONFLICT_LOGIN = "exception.account.conflict.login";
    public static final String ACCOUNT_CONFLICT_EMAIL = "exception.account.conflict.email";
    public static final String ACCOUNT_NOT_BLOCKED = "exception.account.state.not.blocked";
    public static final String ACCOUNT_NOT_ACTIVE = "exception.account.state.not.active";
    public static final String ACCOUNT_ARCHIVAL = "exception.account.state.archival";
    public static final String ACCOUNT_LAST_ROLE = "exception.account.role.last.role";
    public static final String ACCOUNT_ROLE_ALREADY_EXISTS = "exception.account.role.already.exists";
    public static final String ACCOUNT_ROLE_CONFLICT = "exception.account.role.conflict";
    public static final String ACCOUNT_ROLE_NOT_FOUND = "exception.account.role.not.found";
    public static final String ACCOUNT_ROLE_MORE_THAN_ONE = "exception.account.role.more.than.one.assigned";
    public static final String ACCOUNT_ROLE_ASSIGN_GUEST = "exception.account.role.cant.assign.guest";
}
