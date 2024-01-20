package pl.lodz.p.edu.logic.service.api;

import pl.lodz.p.edu.dataaccess.model.entity.Account;

import java.util.Locale;

public interface OwnAccountService {

    Account findByLogin(String login);

    Account updateOwnLocale(String login, Locale locale);

    Account changePassword(String login, String currentPassword, String newPassword);


}
