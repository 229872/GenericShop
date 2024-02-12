package pl.lodz.p.edu.logic.service.impl.decorator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.RequestScope;
import pl.lodz.p.edu.dataaccess.model.entity.Account;
import pl.lodz.p.edu.dataaccess.model.entity.Contact;
import pl.lodz.p.edu.dataaccess.model.enumerated.AccountRole;
import pl.lodz.p.edu.logic.service.api.AccountService;
import pl.lodz.p.edu.logic.service.api.OwnAccountService;

import java.util.List;
import java.util.Locale;

@Service
@RequestScope
@Primary
@Transactional(transactionManager = "accountsModTxManager", propagation = Propagation.NEVER)
@Qualifier("AccountServiceRetryHandler")
class AccountServiceRetryHandler extends AbstractRetryHandler implements AccountService, OwnAccountService {

    private final AccountService accountService;
    private final OwnAccountService ownAccountService;

    public AccountServiceRetryHandler(@Qualifier("AccountServiceImpl") AccountService accountService,
                                      @Qualifier("AccountServiceImpl") OwnAccountService ownAccountService) {
        this.accountService = accountService;
        this.ownAccountService = ownAccountService;
    }

    @Override
    public List<Account> findAll() {
        return repeatTransactionWhenTimeoutOccurred(accountService::findAll);
    }

    @Override
    public List<Account> findAll(Pageable pageable) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountService.findAll(pageable));
    }

    @Override
    public Account findById(Long id) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountService.findById(id));
    }

    @Override
    public Account findByLogin(String login) {
        return repeatTransactionWhenTimeoutOccurred(() -> ownAccountService.findByLogin(login));
    }

    @Override
    public Account create(Account account) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountService.create(account));
    }

    @Override
    public Account updateContactInformation(Long id, Contact newContactData) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountService.updateContactInformation(id, newContactData));
    }

    @Override
    public Account block(Long id) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountService.block(id));
    }

    @Override
    public Account unblock(Long id) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountService.unblock(id));
    }

    @Override
    public Account archive(Long id) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountService.archive(id));
    }

    @Override
    public Account addRole(Long id, AccountRole newRole) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountService.addRole(id, newRole));
    }

    @Override
    public Account removeRole(Long id, AccountRole roleForRemoval) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountService.removeRole(id, roleForRemoval));
    }

    @Override
    public Account changeRole(Long id, AccountRole newRole) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountService.changeRole(id, newRole));
    }

    @Override
    public Account updateOwnLocale(String login, Locale locale) {
        return repeatTransactionWhenTimeoutOccurred(() -> ownAccountService.updateOwnLocale(login, locale));
    }

    @Override
    public Account changePassword(String login, String currentPassword, String newPassword) {
        return repeatTransactionWhenTimeoutOccurred(() -> ownAccountService.changePassword(login, currentPassword, newPassword));
    }
}


