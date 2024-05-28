package pl.lodz.p.edu.shop.presentation.adapter.api;

import pl.lodz.p.edu.shop.presentation.dto.user.account.*;

public interface AccountAccessServiceOperations {

    AccountOutputDto findByLogin(String login);

    AccountOutputDto updateOwnLocale(String login, ChangeLanguageDto locale);

    AccountOutputDto changePassword(String login, ChangePasswordDto passwords);

    AccountOutputDto changeEmail(String login, ChangeEmailDto email);

    AccountOutputDto updateContactInformation(String login, UpdateContactDto updateDto);

    AccountOutputDto register(RegisterDto registerDto);

    void confirmRegistration(String verificationToken);

    void forgotPassword(ForgotPasswordDto forgotPasswordDto);

    void validateResetPasswordToken(String token);

    void resetPassword(ResetPasswordDto resetPasswordDto);
}
