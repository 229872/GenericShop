package pl.lodz.p.edu.shop.presentation.adapter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.logic.service.api.AccountManagementService;
import pl.lodz.p.edu.shop.presentation.adapter.api.AccountManagementServiceOperations;
import pl.lodz.p.edu.shop.presentation.dto.user.account.CreateAccountDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.AccountOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.UpdateContactDto;
import pl.lodz.p.edu.shop.presentation.mapper.api.AccountMapper;

import java.util.List;

@RequiredArgsConstructor

@Component
class AccountManagementManagementServiceAdapter implements AccountManagementServiceOperations {

    private final AccountManagementService accountManagementService;
    private final AccountMapper accountMapper;

    @Override
    public List<AccountOutputDto> findAll() {
        return accountManagementService.findAll().stream()
            .map(accountMapper::mapToAccountOutputDto)
            .toList();
    }

    @Override
    public List<AccountOutputDto> findAll(Pageable pageable) {
        return accountManagementService.findAll(pageable).stream()
            .map(accountMapper::mapToAccountOutputDto)
            .toList();
    }

    @Override
    public AccountOutputDto findById(Long id) {
        Account account = accountManagementService.findById(id);
        return accountMapper.mapToAccountOutputDto(account);
    }

    @Override
    public AccountOutputDto create(CreateAccountDto account) {
        Account outputAccount = accountMapper.mapToAccount(account);
        Account createdAccount = accountManagementService.create(outputAccount);
        return accountMapper.mapToAccountOutputDto(createdAccount);
    }

    @Override
    public AccountOutputDto updateContactInformation(Long id, UpdateContactDto newContactData) {
        return null;
    }

    @Override
    public AccountOutputDto block(Long id) {
        Account account = accountManagementService.block(id);
        return accountMapper.mapToAccountOutputDto(account);
    }

    @Override
    public AccountOutputDto unblock(Long id) {
        Account account = accountManagementService.unblock(id);
        return accountMapper.mapToAccountOutputDto(account);
    }

    @Override
    public AccountOutputDto archive(Long id) {
        Account account = accountManagementService.archive(id);
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
}
