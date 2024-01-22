package pl.lodz.p.edu.presentation.adapter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.lodz.p.edu.dataaccess.model.entity.Account;
import pl.lodz.p.edu.logic.service.api.AccountService;
import pl.lodz.p.edu.logic.service.api.OwnAccountService;
import pl.lodz.p.edu.presentation.adapter.api.AccountServiceOperations;
import pl.lodz.p.edu.presentation.adapter.api.OwnAccountServiceOperations;
import pl.lodz.p.edu.presentation.dto.user.account.*;
import pl.lodz.p.edu.presentation.mapper.api.AccountMapper;

import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor

@Component
class AccountServiceAdapter implements AccountServiceOperations, OwnAccountServiceOperations {

    private final AccountService accountService;
    private final OwnAccountService ownAccountService;
    private final AccountMapper accountMapper;

    @Override
    public List<AccountOutputDto> findAll() {
        return accountService.findAll().stream()
            .map(accountMapper::mapToAccountOutputDto)
            .toList();
    }

    @Override
    public List<AccountOutputDto> findAll(Pageable pageable) {
        return accountService.findAll(pageable).stream()
            .map(accountMapper::mapToAccountOutputDto)
            .toList();
    }

    @Override
    public AccountOutputDto findById(Long id) {
        Account account = accountService.findById(id);
        return accountMapper.mapToAccountOutputDto(account);
    }

    @Override
    public AccountOutputDto create(AccountCreateDto account) {
        Account outputAccount = accountMapper.mapToAccount(account);
        Account createdAccount = accountService.create(outputAccount);
        return accountMapper.mapToAccountOutputDto(createdAccount);
    }

    @Override
    public AccountOutputDto updateContactInformation(Long id, AccountUpdateDto newContactData) {
        return null;
    }

    @Override
    public AccountOutputDto block(Long id) {
        Account account = accountService.block(id);
        return accountMapper.mapToAccountOutputDto(account);
    }

    @Override
    public AccountOutputDto unblock(Long id) {
        Account account = accountService.unblock(id);
        return accountMapper.mapToAccountOutputDto(account);
    }

    @Override
    public AccountOutputDto archive(Long id) {
        Account account = accountService.archive(id);
        return accountMapper.mapToAccountOutputDto(account);
    }

    @Override
    public AccountOutputDto addRole(Long id, String newRole) {
        return null;
    }

    @Override
    public AccountOutputDto removeRole(Long id, String roleForRemoval) {
        return null;
    }

    @Override
    public AccountOutputDto changeRole(Long id, String newRole) {
        return null;
    }

    @Override
    public AccountOutputDto findByLogin(String login) {
        Account account = ownAccountService.findByLogin(login);
        return accountMapper.mapToAccountOutputDto(account);
    }

    @Override
    public AccountOutputDto updateOwnLocale(String login, ChangeLanguageDto locale) {
        Locale language = new Locale(locale.locale());
        Account account = ownAccountService.updateOwnLocale(login, language);
        return accountMapper.mapToAccountOutputDto(account);
    }

    @Override
    public AccountOutputDto changePassword(String login, ChangePasswordDto passwords) {
        Account account = ownAccountService.changePassword(login, passwords.currentPassword(), passwords.newPassword());
        return accountMapper.mapToAccountOutputDto(account);
    }
}
