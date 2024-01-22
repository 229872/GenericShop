package pl.lodz.p.edu.logic.service.impl.decorator;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransactionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.JpaSystemException;
import pl.lodz.p.edu.exception.ApplicationExceptionFactory;
import pl.lodz.p.edu.util.ExceptionUtil;

import java.util.Objects;
import java.util.function.Supplier;

@Slf4j
public abstract class AbstractRetryHandler {

    @Value("${transaction.repeat.times:3}")
    private int transactionRetries;

    protected <T> T repeatTransactionWhenTimeoutOccurred(Supplier<T> supplier) {
        int retryCounter = 1;

        while (retryCounter <= transactionRetries) {
            try {
                log.info("Transaction number {}", retryCounter);
                return supplier.get();
            } catch (JpaSystemException e) {
                TransactionException cause = ExceptionUtil.findCause(e, TransactionException.class);
                if (Objects.nonNull(cause) && cause.getMessage().contains("timeout")) {
                    log.warn("Transaction number {} failed", retryCounter);
                    retryCounter++;
                } else {
                    break;
                }
            }
        }

        throw ApplicationExceptionFactory.createTransactionTimeoutException();
    }
}
