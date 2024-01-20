package pl.lodz.p.edu.logic.service.api;

import org.springframework.data.domain.Pageable;
import pl.lodz.p.edu.dataaccess.model.entity.Account;
import pl.lodz.p.edu.dataaccess.model.enumerated.AccountRole;
import pl.lodz.p.edu.logic.model.NewContactData;

import java.util.List;

public interface AccountService {

    List<Account> findAll();

    List<Account> findAll(Pageable pageable);

    Account findById(Long id);

    Account create(Account account);

    Account updateContactInformation(Long id, NewContactData newContactData);

    Account block(Long id);

    Account unblock(Long id);

    Account archive(Long id);

    Account addRole(Long id, AccountRole newRole);

    Account removeRole(Long id, AccountRole roleForRemoval);

    Account changeRole(Long id, AccountRole newRole);


}
