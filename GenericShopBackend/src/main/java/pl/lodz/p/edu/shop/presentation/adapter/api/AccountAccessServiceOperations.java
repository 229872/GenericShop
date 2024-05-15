package pl.lodz.p.edu.shop.presentation.adapter.api;

import pl.lodz.p.edu.shop.presentation.dto.user.account.AccountOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.AccountRegisterDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.ChangeLanguageDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.ChangePasswordDto;

public interface AccountAccessServiceOperations {

    AccountOutputDto findByLogin(String login);

    AccountOutputDto updateOwnLocale(String login, ChangeLanguageDto locale);

    AccountOutputDto changePassword(String login, ChangePasswordDto passwords);

    AccountOutputDto register(AccountRegisterDto registerDto);

    void confirmRegistration(String verificationToken);
}
