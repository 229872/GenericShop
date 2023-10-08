package pl.lodz.p.edu.logic.service.api;

import org.springframework.data.domain.Pageable;
import pl.lodz.p.edu.dataaccess.model.Account;
import pl.lodz.p.edu.dataaccess.model.sub.AccountRole;
import pl.lodz.p.edu.logic.model.NewPersonalInformation;

import java.util.List;
import java.util.Locale;

public interface AccountService {

    List<Account> findAll();

    List<Account> findAll(Pageable pageable);

    Account findById(Long id);

    Account findByLogin(String login);

    Account create(Account account);

    Account updatePersonalInformation(Long id, NewPersonalInformation newPersonalInformation);

    Account block(Long id);

    Account unblock(Long id);

    Account archive(Long id);

    Account addRole(Long id, AccountRole newRole);

    Account removeRole(Long id, AccountRole roleForRemoval);

    Account changeRole(Long id, AccountRole oldRole, AccountRole newRole);

    Account updateLocale(Long id, Locale locale);
}
