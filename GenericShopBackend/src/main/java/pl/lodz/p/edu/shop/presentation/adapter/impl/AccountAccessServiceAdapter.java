package pl.lodz.p.edu.shop.presentation.adapter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.logic.service.api.AccountAccessService;
import pl.lodz.p.edu.shop.presentation.adapter.api.AccountAccessServiceOperations;
import pl.lodz.p.edu.shop.presentation.dto.user.account.AccountOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.AccountRegisterDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.ChangeLanguageDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.ChangePasswordDto;
import pl.lodz.p.edu.shop.presentation.mapper.api.AccountMapper;

import java.util.Locale;

@RequiredArgsConstructor

@Component
class AccountAccessServiceAdapter implements AccountAccessServiceOperations {

    private final AccountAccessService accountAccessService;
    private final AccountMapper accountMapper;

    @Override
    public AccountOutputDto findByLogin(String login) {
        Account account = accountAccessService.findByLogin(login);
        return accountMapper.mapToAccountOutputDto(account);
    }

    @Override
    public AccountOutputDto updateOwnLocale(String login, ChangeLanguageDto locale) {
        Locale language = new Locale(locale.locale());
        Account account = accountAccessService.updateOwnLocale(login, language);
        return accountMapper.mapToAccountOutputDto(account);
    }

    @Override
    public AccountOutputDto changePassword(String login, ChangePasswordDto passwords) {
        Account account = accountAccessService.changePassword(login, passwords.currentPassword(), passwords.newPassword());
        return accountMapper.mapToAccountOutputDto(account);
    }

    @Override
    public AccountOutputDto register(AccountRegisterDto registerDto) {
        Account account = accountMapper.mapToAccount(registerDto);
        Account registeredAccount = accountAccessService.register(account);
        return accountMapper.mapToAccountOutputDto(registeredAccount);
    }
}
