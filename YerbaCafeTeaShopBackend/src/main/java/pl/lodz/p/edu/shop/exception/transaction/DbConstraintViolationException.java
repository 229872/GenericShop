package pl.lodz.p.edu.shop.exception.transaction;

public class DbConstraintViolationException extends RuntimeException {

    public DbConstraintViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
