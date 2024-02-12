package pl.lodz.p.edu.shop.presentation.mapper.api;

import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.presentation.dto.user.account.AccountCreateDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.AccountOutputDto;

public interface AccountMapper {

    Account mapToAccount(AccountCreateDto createDto);

    AccountOutputDto mapToAccountOutputDto(Account account);
}
