package pl.lodz.p.edu.shop.logic.service.api;

import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;

import java.util.Locale;

public interface OwnAccountService {

    Account findByLogin(String login);

    Account updateOwnLocale(String login, Locale locale);

    Account changePassword(String login, String currentPassword, String newPassword);


}
