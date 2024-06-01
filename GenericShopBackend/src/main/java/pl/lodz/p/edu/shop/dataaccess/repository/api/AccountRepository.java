package pl.lodz.p.edu.shop.dataaccess.repository.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;

import java.util.List;
import java.util.Optional;


public interface AccountRepository {

    List<Account> findAll();

    Page<Account> findAll(Pageable pageable);

    Optional<Account> findById(Long id);

    Optional<Account> findByLogin(String login);

    Optional<Account> findByEmail(String email);

    Account save(Account account);

    void flush();
}
