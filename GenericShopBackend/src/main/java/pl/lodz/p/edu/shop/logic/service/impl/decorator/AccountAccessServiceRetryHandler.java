package pl.lodz.p.edu.shop.logic.service.impl.decorator;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.RequestScope;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Contact;
import pl.lodz.p.edu.shop.logic.service.api.AccountAccessService;

import java.util.Locale;

@Service
@RequestScope
@Primary
@Transactional(transactionManager = "accountsModTxManager", propagation = Propagation.NEVER)
@Qualifier("AccountAccessServiceRetryHandler")
class AccountAccessServiceRetryHandler extends AbstractRetryHandler implements AccountAccessService {

    private final AccountAccessService accountAccessService;

    public AccountAccessServiceRetryHandler(@Qualifier("AccountAccessServiceImpl")
                                            AccountAccessService accountAccessService) {
        this.accountAccessService = accountAccessService;
    }

    @Override
    public Account findByLogin(String login) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountAccessService.findByLogin(login));
    }

    @Override
    public Account updateOwnLocale(String login, Locale locale) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountAccessService.updateOwnLocale(login, locale));
    }

    @Override
    public Account changePassword(String login, String currentPassword, String newPassword) {
        return repeatTransactionWhenTimeoutOccurred(
            () -> accountAccessService.changePassword(login, currentPassword, newPassword)
        );
    }

    @Override
    public Account changeEmail(String login, String email) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountAccessService.changeEmail(login, email));
    }

    @Override
    public Account updateContactInformation(String login, Contact newContactData, Long frontendContactVersion) {
        return repeatTransactionWhenTimeoutOccurred(
            () -> accountAccessService.updateContactInformation(login, newContactData, frontendContactVersion)
        );
    }

    @Override
    public Account register(Account account) {
        return repeatTransactionWhenTimeoutOccurred(() -> accountAccessService.register(account));
    }

    @Override
    public void confirmRegistration(String verificationToken) {
        repeatTransactionWhenTimeoutOccurred(() -> accountAccessService.confirmRegistration(verificationToken));
    }

    @Override
    public void forgotPassword(String email) {
        repeatTransactionWhenTimeoutOccurred(() -> accountAccessService.forgotPassword(email));
    }

    @Override
    public void validateResetPasswordToken(String token) {
        repeatTransactionWhenTimeoutOccurred(() -> accountAccessService.validateResetPasswordToken(token));
    }

    @Override
    public void resetPassword(String password,  String resetPasswordToken) {
        repeatTransactionWhenTimeoutOccurred(() -> accountAccessService.resetPassword(password, resetPasswordToken));
    }
}
