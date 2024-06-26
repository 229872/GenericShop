package pl.lodz.p.edu.shop.logic.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Contact;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountRole;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountState;
import pl.lodz.p.edu.shop.dataaccess.repository.api.AccountRepository;
import pl.lodz.p.edu.shop.exception.ApplicationExceptionFactory;
import pl.lodz.p.edu.shop.exception.account.helper.AccountStateOperation;
import pl.lodz.p.edu.shop.logic.service.api.AccountManagementService;

import java.util.List;
import java.util.Set;

@Service
@Transactional(transactionManager = "accountsModTxManager", propagation = Propagation.REQUIRES_NEW)
@Qualifier("AccountManagementServiceImpl")
class AccountManagementServiceImpl extends AccountService implements AccountManagementService {

    private final AccountRepository accountRepository;

    public AccountManagementServiceImpl(AccountRepository accountRepository) {
        super(accountRepository);
        this.accountRepository = accountRepository;
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Page<Account> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);
    }

    @Override
    public Account create(Account account) {
        Set<AccountRole> roles = account.getAccountRoles();

        if (roles.size() > 1) {
            throw ApplicationExceptionFactory.createCantCreateAccountWithManyRolesException();
        }

        if (roles.contains(AccountRole.GUEST)) {
            throw ApplicationExceptionFactory.createCantAssignGuestRoleException();
        }

        if (account.getAccountState().equals(AccountState.NOT_VERIFIED)) {
            throw ApplicationExceptionFactory.createCantCreateAccountWithNotVerifiedStatusException();
        }

        return save(account);
    }

    @Override
    public Account block(Long id) {
        Account account = accountRepository.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

        if (account.isArchival()) {
            throw ApplicationExceptionFactory.createCantModifyArchivalAccountException();
        }

        if (!account.getAccountState().equals(AccountState.ACTIVE)) {
            throw ApplicationExceptionFactory
                .createOperationNotAllowedWithActualAccountStateException(AccountStateOperation.BLOCK);
        }

        account.setAccountState(AccountState.BLOCKED);
        return save(account);
    }

    @Override
    public Account unblock(Long id) {
        Account account = accountRepository.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

        if (account.isArchival()) {
            throw ApplicationExceptionFactory.createCantModifyArchivalAccountException();
        }

        if (!account.getAccountState().equals(AccountState.BLOCKED)) {
            throw ApplicationExceptionFactory
                .createOperationNotAllowedWithActualAccountStateException(AccountStateOperation.UNBLOCK);
        }

        account.setAccountState(AccountState.ACTIVE);
        return save(account);
    }

    @Override
    public Account archive(Long id) {
        Account account = accountRepository.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

        if (account.isArchival()) {
            throw ApplicationExceptionFactory.createCantModifyArchivalAccountException();
        }

        archiveAccount(account);
        return save(account);
    }

    @Override
    public Account addRole(Long id, AccountRole newRole) {
        Account account = accountRepository.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);
        Set<AccountRole> roles = account.getAccountRoles();

        if (account.isArchival()) {
            throw ApplicationExceptionFactory.createCantModifyArchivalAccountException();
        }

        if (roles.contains(newRole)) {
            throw ApplicationExceptionFactory.createAccountRoleAlreadyAssignedException();
        }

        if (newRole.equals(AccountRole.GUEST)) {
            throw ApplicationExceptionFactory.createCantAssignGuestRoleException();
        }

        if (newRole.equals(AccountRole.ADMIN) || roles.contains(AccountRole.ADMIN)) {
            throw ApplicationExceptionFactory.createAccountWithAdministratorRoleCantHaveMoreRolesException();
        }

        roles.add(newRole);
        return save(account);
    }

    @Override
    public Account removeRole(Long id, AccountRole roleForRemoval) {
        Account account = accountRepository.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);
        Set<AccountRole> roles = account.getAccountRoles();

        if (account.isArchival()) {
            throw ApplicationExceptionFactory.createCantModifyArchivalAccountException();
        }

        if (!roles.contains(roleForRemoval)) {
            throw ApplicationExceptionFactory.createAccountRoleNotFoundException();
        }

        if (roles.size() == 1) {
            throw ApplicationExceptionFactory.createCantRemoveLastRoleException();
        }

        roles.remove(roleForRemoval);
        return save(account);
    }

    @Override
    public Account changeRole(Long id, AccountRole newRole) {
        Account account = accountRepository.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);
        Set<AccountRole> roles = account.getAccountRoles();

        if (account.isArchival()) {
            throw ApplicationExceptionFactory.createCantModifyArchivalAccountException();
        }

        if (roles.size() > 1) {
            throw ApplicationExceptionFactory.createCantChangeRoleIfMoreThanOneAlreadyAssignedException();
        }

        if (roles.contains(newRole)) {
            throw ApplicationExceptionFactory.createAccountRoleAlreadyAssignedException();
        }

        if (newRole.equals(AccountRole.GUEST)) {
            throw ApplicationExceptionFactory.createCantAssignGuestRoleException();
        }

        roles.remove(roles.iterator().next());
        roles.add(newRole);

        return save(account);
    }

    private void archiveAccount(Account account) {
        Contact contact = account.getContact();
        account.setArchival(true);
        contact.setArchival(true);
        contact.getAddress().setArchival(true);
    }
}
