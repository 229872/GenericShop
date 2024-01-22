package pl.lodz.p.edu.presentation.adapter.api;

import org.springframework.data.domain.Pageable;
import pl.lodz.p.edu.presentation.dto.user.account.AccountCreateDto;
import pl.lodz.p.edu.presentation.dto.user.account.AccountOutputDto;
import pl.lodz.p.edu.presentation.dto.user.account.AccountUpdateDto;

import java.util.List;

public interface AccountServiceOperations {

    List<AccountOutputDto> findAll();

    List<AccountOutputDto> findAll(Pageable pageable);

    AccountOutputDto findById(Long id);

    AccountOutputDto create(AccountCreateDto account);

    AccountOutputDto updateContactInformation(Long id, AccountUpdateDto newContactData);

    AccountOutputDto block(Long id);

    AccountOutputDto unblock(Long id);

    AccountOutputDto archive(Long id);

    AccountOutputDto addRole(Long id, String newRole);

    AccountOutputDto removeRole(Long id, String roleForRemoval);

    AccountOutputDto changeRole(Long id, String newRole);
}
