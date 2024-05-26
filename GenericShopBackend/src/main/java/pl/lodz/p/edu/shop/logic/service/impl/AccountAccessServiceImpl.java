package pl.lodz.p.edu.shop.logic.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountRole;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountState;
import pl.lodz.p.edu.shop.dataaccess.repository.api.AccountRepository;
import pl.lodz.p.edu.shop.exception.ApplicationExceptionFactory;
import pl.lodz.p.edu.shop.exception.account.helper.AccountStateOperation;
import pl.lodz.p.edu.shop.logic.service.api.AccountAccessService;
import pl.lodz.p.edu.shop.logic.service.api.JwtService;
import pl.lodz.p.edu.shop.logic.service.api.MailService;

import java.util.Locale;
import java.util.Set;

@Service
@Transactional(transactionManager = "accountsModTxManager", propagation = Propagation.REQUIRES_NEW)
@Qualifier("AccountAccessServiceImpl")
class AccountAccessServiceImpl extends AccountService implements AccountAccessService {

    private final AccountRepository accountRepository;
    private final MailService mailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AccountAccessServiceImpl(
        AccountRepository accountRepository, PasswordEncoder passwordEncoder,
        MailService mailService, JwtService jwtService
    ) {
        super(accountRepository);
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.jwtService = jwtService;
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

        return save(account);
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

    @Override
    public Account changeEmail(String login, String email) {
        Account account = accountRepository.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

        if (account.isArchival()) {
            throw ApplicationExceptionFactory.createCantModifyArchivalAccountException();
        }

        account.setEmail(email);

        return save(account);
    }

    @Override
    public Account register(Account account) {
        String verificationToken = jwtService.generateVerificationToken(account.getLogin(), account.getEmail());
        mailService.sendVerificationMail(account.getEmail(), account.getLocale(), verificationToken);

        account.setAccountState(AccountState.NOT_VERIFIED);
        account.setAccountRoles(Set.of(AccountRole.CLIENT));

        return save(account);
    }

    @Override
    public void confirmRegistration(String verificationToken) {
        String login = jwtService.decodeSubjectFromJwtTokenWithoutValidation(verificationToken);
        Account account = accountRepository.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createExpiredTokenException);

        jwtService.validateVerificationToken(verificationToken, account.getEmail());

        account.setAccountState(AccountState.ACTIVE);

        save(account);
    }

    @Override
    public void forgotPassword(String email) {
        accountRepository.findByEmail(email)
            .ifPresent(account -> {
                String token = jwtService.generateResetPasswordToken(account.getLogin(), account.getPassword());
                mailService.sendResetPasswordMail(email, account.getLocale(), token);
            });
    }

    private Account verifyResetPasswordToken(String resetPasswordToken) {
        String login = jwtService.decodeSubjectFromJwtTokenWithoutValidation(resetPasswordToken);
        Account account = accountRepository.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createExpiredTokenException);

        if (!account.getAccountState().equals(AccountState.ACTIVE)) {
            throw ApplicationExceptionFactory
                .createOperationNotAllowedWithActualAccountStateException(AccountStateOperation.BLOCK);
        }

        if (account.isArchival()) {
            throw ApplicationExceptionFactory.createCantModifyArchivalAccountException();
        }

        jwtService.validateResetPasswordToken(resetPasswordToken, account.getPassword());
        return account;
    }

    @Override
    public void validateResetPasswordToken(String resetPasswordToken) {
        verifyResetPasswordToken(resetPasswordToken);
    }

    @Override
    public void resetPassword(String password, String resetPasswordToken) {
        Account account = verifyResetPasswordToken(resetPasswordToken);
        account.setPassword(passwordEncoder.encode(password));
        save(account);
    }
}
