package pl.lodz.p.edu.shop.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.lodz.p.edu.shop.exception.other.DecodeException;
import pl.lodz.p.edu.shop.exception.transaction.DbConstraintViolationException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SystemExceptionFactory {

    public static DbConstraintViolationException createDbConstraintViolationException(Throwable cause) {
        return new DbConstraintViolationException(ExceptionMessage.DB_CONSTRAINT_VIOLATION, cause);
    }

    public static DecodeException createDecodeException(Throwable cause) {
        return new DecodeException(ExceptionMessage.DECODE_EXCEPTION, cause);
    }
}
