package pl.lodz.p.edu.shop.logic.service.impl.decorator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.RequestScope;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Contact;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountRole;
import pl.lodz.p.edu.shop.logic.service.api.AccountManagementService;

import java.util.List;

@Service
@RequestScope
@Primary
@Transactional(transactionManager = "accountsModTxManager", propagation = Propagation.NEVER)
@Qualifier("AccountServiceRetryHandler")
class AccountManagementServiceRetryHandler extends AbstractRetryHandler implements AccountManagementService {

    private final AccountManagementService accountManagementService;

    public AccountManagementServiceRetryHandler(@Qualifier("AccountManagementServiceImpl")
                                                AccountManagementService accountManagementService) {
        this.accountManagementService = accountManagementService;
    }

    @Override
    public List<Account> findAll() {
        return repeatTransactionWhenTimeoutOccurred(() -> accountManagementService.findAll());
    }

    @Override
    public Page<Account> findAll(Pageable pageable) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountManagementService.findAll(pageable));
    }

    @Override
    public Account findById(Long id) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountManagementService.findById(id));
    }

    @Override
    public Account create(Account account) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountManagementService.create(account));
    }

    @Override
    public Account updateContactInformation(Long id, Contact newContactData) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountManagementService.updateContactInformation(id, newContactData));
    }

    @Override
    public Account block(Long id) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountManagementService.block(id));
    }

    @Override
    public Account unblock(Long id) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountManagementService.unblock(id));
    }

    @Override
    public Account archive(Long id) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountManagementService.archive(id));
    }

    @Override
    public Account addRole(Long id, AccountRole newRole) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountManagementService.addRole(id, newRole));
    }

    @Override
    public Account removeRole(Long id, AccountRole roleForRemoval) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountManagementService.removeRole(id, roleForRemoval));
    }

    @Override
    public Account changeRole(Long id, AccountRole newRole) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountManagementService.changeRole(id, newRole));
    }
}


