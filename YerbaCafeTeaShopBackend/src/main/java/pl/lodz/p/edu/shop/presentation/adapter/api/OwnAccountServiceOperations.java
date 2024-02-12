package pl.lodz.p.edu.shop.presentation.adapter.api;

import pl.lodz.p.edu.shop.presentation.dto.user.account.AccountOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.ChangeLanguageDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.ChangePasswordDto;

public interface OwnAccountServiceOperations {

    AccountOutputDto findByLogin(String login);

    AccountOutputDto updateOwnLocale(String login, ChangeLanguageDto locale);

    AccountOutputDto changePassword(String login, ChangePasswordDto passwords);
}
