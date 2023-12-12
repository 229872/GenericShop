package pl.lodz.p.edu.util;

public class ExceptionUtil {
    public static <T extends Throwable> T findCause(Throwable throwable, Class<T> causeType) {
        Throwable currentThrowable = throwable;

        while (currentThrowable != null) {
            if (causeType.isAssignableFrom(currentThrowable.getClass())) {
                // Cast to the desired type and return
                return (T) currentThrowable;
            }
            currentThrowable = currentThrowable.getCause();
        }

        return null;
    }
}
