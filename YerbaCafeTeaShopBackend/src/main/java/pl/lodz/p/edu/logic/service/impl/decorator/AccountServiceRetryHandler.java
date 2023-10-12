package pl.lodz.p.edu.logic.service.impl.decorator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.RequestScope;
import pl.lodz.p.edu.dataaccess.model.Account;
import pl.lodz.p.edu.dataaccess.model.sub.AccountRole;
import pl.lodz.p.edu.logic.model.NewPersonalInformation;
import pl.lodz.p.edu.logic.service.AbstractRetryHandler;
import pl.lodz.p.edu.logic.service.api.AccountService;

import java.util.List;
import java.util.Locale;


@Service
@RequestScope
@Transactional(propagation = Propagation.NEVER)
@Qualifier("AccountServiceRetryHandler")
@Primary
public class AccountServiceRetryHandler extends AbstractRetryHandler implements AccountService {

    private final AccountService accountService;

    public AccountServiceRetryHandler(@Qualifier("AccountServiceImpl") AccountService accountService) {
        this.accountService = accountService;
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
        return repeatTransactionWhenTimeoutOccurred(() -> accountService.findByLogin(login));
    }

    @Override
    public Account create(Account account) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountService.create(account));
    }

    @Override
    public Account updatePersonalInformation(Long id, NewPersonalInformation newPersonalInformation) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountService.updatePersonalInformation(id, newPersonalInformation));
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
    public Account changeRole(Long id, AccountRole oldRole, AccountRole newRole) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountService.changeRole(id, oldRole, newRole));
    }

    @Override
    public Account updateLocale(Long id, Locale locale) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountService.updateLocale(id, locale));
    }
}
