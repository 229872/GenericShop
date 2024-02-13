package pl.lodz.p.edu.shop.logic.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.repository.api.AccountRepository;
import pl.lodz.p.edu.shop.exception.ApplicationExceptionFactory;
import pl.lodz.p.edu.shop.logic.service.api.AccountAccessService;

import java.util.Locale;

@Service
@Transactional(transactionManager = "accountsModTxManager", propagation = Propagation.REQUIRES_NEW)
@Qualifier("AccountAccessServiceImpl")
class AccountAccessServiceImpl extends AccountManagementAccessServiceImpl implements AccountAccessService {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    public AccountAccessServiceImpl(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        super(accountRepository);
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Account findByLogin(String login) {
        return accountRepository.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);
    }

    @Override
    public Account updateOwnLocale(String login, Locale locale) {
        Account account = accountRepository.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

        account.setLocale(locale.getLanguage());

        return accountRepository.save(account);
    }

    @Override
    public Account changePassword(String login, String currentPassword, String newPassword) {
        Account account = accountRepository.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

        if (!passwordEncoder.matches(currentPassword, account.getPassword())) {
            throw ApplicationExceptionFactory.createInvalidCredentialsException();
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        account.setPassword(encodedNewPassword);

        return save(account);
    }
}
