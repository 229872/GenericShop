package pl.lodz.p.edu.shop.config.database.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.transaction.TransactionExecution;
import org.springframework.transaction.TransactionExecutionListener;
import pl.lodz.p.edu.shop.util.SecurityUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class AccountsModuleTxLogsListener implements TransactionExecutionListener {

    private final Map<TransactionExecution, UUID> mapTxWithId = Collections.synchronizedMap(new HashMap<>());

    @Override
    public void afterBegin(@NonNull TransactionExecution transaction, Throwable beginFailure) {
        UUID transactionId = UUID.randomUUID();
        mapTxWithId.put(transaction, transactionId);

        log.info("TX id={}: AccountsModule transaction STARTED with identity: {} in {}", transactionId,
            SecurityUtil.getLoginFromSecurityContext(), transaction.getTransactionName());
    }

    @Override
    public void afterCommit(@NonNull TransactionExecution transaction, Throwable commitFailure) {
        UUID transactionId = mapTxWithId.get(transaction);

        log.info("TX id={}: AccountsModule transaction ENDS with status COMMIT, identity: {} in {}", transactionId,
            SecurityUtil.getLoginFromSecurityContext(), transaction.getTransactionName());

        mapTxWithId.remove(transaction);
    }

    @Override
    public void afterRollback(@NonNull TransactionExecution transaction, Throwable rollbackFailure) {
        UUID transactionId = mapTxWithId.get(transaction);

        log.info("TX id={}: AccountsModule transaction ENDS with status ROLLBACK, identity: {} in {}", transactionId,
            SecurityUtil.getLoginFromSecurityContext(), transaction.getTransactionName());

        mapTxWithId.remove(transaction);
    }

}
