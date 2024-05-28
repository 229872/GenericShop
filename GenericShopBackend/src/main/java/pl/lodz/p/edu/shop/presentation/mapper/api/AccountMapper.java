package pl.lodz.p.edu.shop.presentation.mapper.api;

import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Contact;
import pl.lodz.p.edu.shop.presentation.dto.user.account.CreateAccountDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.AccountOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.RegisterDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.UpdateContactDto;

public interface AccountMapper {

    Account mapToAccount(CreateAccountDto createDto);

    Account mapToAccount(RegisterDto registerDto);

    Contact mapToContact(UpdateContactDto updateDto);

    AccountOutputDto mapToAccountOutputDto(Account account);

}
