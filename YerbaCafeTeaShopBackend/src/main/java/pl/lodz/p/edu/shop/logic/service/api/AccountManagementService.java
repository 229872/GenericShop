package pl.lodz.p.edu.shop.logic.service.api;

import org.springframework.data.domain.Pageable;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Contact;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountRole;

import java.util.List;

public interface AccountManagementService {

    List<Account> findAll();

    List<Account> findAll(Pageable pageable);

    Account findById(Long id);

    Account create(Account account);

    Account updateContactInformation(Long id, Contact newContactData);

    Account block(Long id);

    Account unblock(Long id);

    Account archive(Long id);

    Account addRole(Long id, AccountRole newRole);

    Account removeRole(Long id, AccountRole roleForRemoval);

    Account changeRole(Long id, AccountRole newRole);


}
