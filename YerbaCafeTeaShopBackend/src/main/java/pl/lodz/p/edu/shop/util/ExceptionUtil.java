package pl.lodz.p.edu.shop.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionUtil {

    public static <T extends Throwable> T findCause(Throwable throwable, Class<T> causeType) {
        Throwable currentThrowable = throwable;

        while (currentThrowable != null) {
            if (causeType.isInstance(currentThrowable)) {
                return causeType.cast(currentThrowable);
            }
            currentThrowable = currentThrowable.getCause();
        }

        return null;
    }
}
