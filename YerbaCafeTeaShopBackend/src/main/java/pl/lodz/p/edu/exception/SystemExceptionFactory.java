package pl.lodz.p.edu.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.lodz.p.edu.exception.transaction.DbConstraintViolationException;

import static pl.lodz.p.edu.exception.ExceptionMessage.DB_CONSTRAINT_VIOLATION;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SystemExceptionFactory {

    public static DbConstraintViolationException createDbConstraintViolationException(Throwable cause) {
        return new DbConstraintViolationException(DB_CONSTRAINT_VIOLATION, cause);
    }
}
