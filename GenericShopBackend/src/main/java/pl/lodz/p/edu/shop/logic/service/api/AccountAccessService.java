package pl.lodz.p.edu.shop.logic.service.api;

import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;

import java.util.Locale;

public interface AccountAccessService {

    Account findByLogin(String login);

    Account updateOwnLocale(String login, Locale locale);

    Account changePassword(String login, String currentPassword, String newPassword);

    Account changeEmail(String login, String email);

    Account register(Account account);

    void confirmRegistration(String verificationToken);

    void forgotPassword(String email);

    void validateResetPasswordToken(String resetPasswordToken);

    void resetPassword(String password, String resetPasswordToken);
}
