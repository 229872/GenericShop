package pl.lodz.p.edu.shop.presentation.adapter.api;

import pl.lodz.p.edu.shop.presentation.dto.user.account.*;

public interface AccountAccessServiceOperations {

    AccountOutputDto findByLogin(String login);

    AccountOutputDto updateOwnLocale(String login, ChangeLanguageDto locale);

    AccountOutputDto changePassword(String login, ChangePasswordDto passwords);

    AccountOutputDto register(AccountRegisterDto registerDto);

    void confirmRegistration(String verificationToken);

    void forgotPassword(ForgotPasswordDto forgotPasswordDto);
}
