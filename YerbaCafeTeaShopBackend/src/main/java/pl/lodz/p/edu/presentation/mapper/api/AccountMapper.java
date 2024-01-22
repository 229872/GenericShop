package pl.lodz.p.edu.presentation.mapper.api;

import pl.lodz.p.edu.dataaccess.model.entity.Account;
import pl.lodz.p.edu.presentation.dto.user.account.AccountCreateDto;
import pl.lodz.p.edu.presentation.dto.user.account.AccountOutputDto;

public interface AccountMapper {

    Account mapToAccount(AccountCreateDto createDto);

    AccountOutputDto mapToAccountOutputDto(Account account);
}
