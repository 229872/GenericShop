package pl.lodz.p.edu.config.database.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionExecution;
import org.springframework.transaction.TransactionExecutionListener;
import pl.lodz.p.edu.util.SecurityUtil;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j

@Component
public class AccountsModuleTxLogsListener implements TransactionExecutionListener {

    private String transactionId;

    @Override
    public void afterBegin(TransactionExecution transaction, Throwable beginFailure) {
        transactionId = Long.toString(System.currentTimeMillis())
            + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);

        log.info("Transaction in AccountsModule with TXid={} in {} started, identity: {}", transactionId,
            transaction.getTransactionName(), SecurityUtil.getLoginFromSecurityContext());
    }

    @Override
    public void afterCommit(TransactionExecution transaction, Throwable commitFailure) {
        log.info("Transaction in AccountsModule with TXid={} in {} ends with status COMMIT, identity: {}",
            transactionId, transaction.getTransactionName(), SecurityUtil.getLoginFromSecurityContext());
    }

    @Override
    public void afterRollback(TransactionExecution transaction, Throwable rollbackFailure) {
        log.info("Transaction in AccountsModule with TXid={} in {} ends with status ROLLBACK, identity: {}",
            transactionId, transaction.getTransactionName(), SecurityUtil.getLoginFromSecurityContext());
    }
}
