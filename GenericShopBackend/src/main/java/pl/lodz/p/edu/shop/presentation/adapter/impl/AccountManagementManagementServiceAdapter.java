package pl.lodz.p.edu.shop.presentation.adapter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountRole;
import pl.lodz.p.edu.shop.logic.service.api.AccountManagementService;
import pl.lodz.p.edu.shop.presentation.adapter.api.AccountManagementServiceOperations;
import pl.lodz.p.edu.shop.presentation.dto.user.account.AccountOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.CreateAccountDto;
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
            .map(accountMapper::mapToAccountOutputDtoWithoutVersion)
            .toList();
    }

    @Override
    public Page<AccountOutputDto> findAll(Pageable pageable) {
        List<Sort.Order> orders = pageable.getSort().stream()
            .map(order -> switch (order.getProperty()) {
                case "archival" -> new Sort.Order(order.getDirection(), "isArchival");
                case "firstName" -> new Sort.Order(order.getDirection(), "contact.firstName");
                case "lastName" -> new Sort.Order(order.getDirection(), "contact.lastName");
                default -> order;
            })
            .toList();
        Sort sort = Sort.by(orders);
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return accountManagementService.findAll(pageRequest)
            .map(accountMapper::mapToAccountOutputDtoWithoutVersion);

    }

    @Override
    public AccountOutputDto findById(Long id) {
        Account account = accountManagementService.findById(id);
        return accountMapper.mapToAccountOutputDtoWithoutVersion(account);
    }

    @Override
    public AccountOutputDto create(CreateAccountDto account) {
        Account outputAccount = accountMapper.mapToAccount(account);
        Account createdAccount = accountManagementService.create(outputAccount);
        return accountMapper.mapToAccountOutputDtoWithoutVersion(createdAccount);
    }

    @Override
    public AccountOutputDto block(Long id) {
        Account account = accountManagementService.block(id);
        return accountMapper.mapToAccountOutputDtoWithoutVersion(account);
    }

    @Override
    public AccountOutputDto unblock(Long id) {
        Account account = accountManagementService.unblock(id);
        return accountMapper.mapToAccountOutputDtoWithoutVersion(account);
    }

    @Override
    public AccountOutputDto archive(Long id) {
        Account account = accountManagementService.archive(id);
        return accountMapper.mapToAccountOutputDtoWithoutVersion(account);
    }

    @Override
    public AccountOutputDto addRole(Long id, String newRole) {
        Account account = accountManagementService.addRole(id, AccountRole.valueOf(newRole));
        return accountMapper.mapToAccountOutputDtoWithoutVersion(account);
    }

    @Override
    public AccountOutputDto removeRole(Long id, String roleForRemoval) {
        Account account = accountManagementService.removeRole(id, AccountRole.valueOf(roleForRemoval));
        return accountMapper.mapToAccountOutputDtoWithoutVersion(account);
    }

    @Override
    public AccountOutputDto changeRole(Long id, String newRole) {
        Account account = accountManagementService.changeRole(id, AccountRole.valueOf(newRole));
        return accountMapper.mapToAccountOutputDtoWithoutVersion(account);
    }
}
