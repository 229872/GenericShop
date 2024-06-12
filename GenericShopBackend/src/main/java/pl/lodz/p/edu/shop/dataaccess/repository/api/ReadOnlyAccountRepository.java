package pl.lodz.p.edu.shop.dataaccess.repository.api;

import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;

import java.util.Optional;

public interface ReadOnlyAccountRepository {

    Optional<Account> findByLogin(String login);

    void flush();
}
