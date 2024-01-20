package pl.lodz.p.edu.exception.transaction;

public class DbConstraintViolationException extends RuntimeException {

    public DbConstraintViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
