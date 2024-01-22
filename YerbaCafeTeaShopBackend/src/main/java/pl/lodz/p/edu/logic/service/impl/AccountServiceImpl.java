package pl.lodz.p.edu.logic.service.impl;

import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.edu.dataaccess.model.entity.Account;
import pl.lodz.p.edu.dataaccess.model.entity.Contact;
import pl.lodz.p.edu.dataaccess.model.enumerated.AccountRole;
import pl.lodz.p.edu.dataaccess.model.enumerated.AccountState;
import pl.lodz.p.edu.dataaccess.repository.api.AccountRepository;
import pl.lodz.p.edu.exception.ApplicationExceptionFactory;
import pl.lodz.p.edu.exception.SystemExceptionFactory;
import pl.lodz.p.edu.exception.account.helper.AccountStateOperation;
import pl.lodz.p.edu.logic.model.NewContactData;
import pl.lodz.p.edu.logic.service.api.AccountService;
import pl.lodz.p.edu.logic.service.api.OwnAccountService;
import pl.lodz.p.edu.util.ExceptionUtil;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import static pl.lodz.p.edu.util.UpdatableUtil.setNullableValue;


@RequiredArgsConstructor

@Service
@Transactional(transactionManager = "accountsModTxManager", propagation = Propagation.REQUIRES_NEW)
@Qualifier("AccountServiceImpl")
public class AccountServiceImpl implements AccountService, OwnAccountService {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public List<Account> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable).stream().toList();
    }

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);
    }

    @Override
    public Account findByLogin(String login) {
        return accountRepository.findByLogin(login)
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
    public Account updateContactInformation(Long id, NewContactData newContactData) {
        Account account = accountRepository.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

        if (account.isArchival()) {
            throw ApplicationExceptionFactory.createCantModifyArchivalAccountException();
        }

        updatePersonalInformation(account, newContactData);
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

    @Override
    public Account updateOwnLocale(String login, Locale locale) {
        Account account = accountRepository.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

        account.setLocale(locale.getLanguage());

        return accountRepository.save(account);
    }

    @Override
    public Account changePassword(String login, String currentPassword, String newPassword) {
        Account account = accountRepository.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

        if (!passwordEncoder.matches(currentPassword, account.getPassword())) {
            throw ApplicationExceptionFactory.createInvalidCredentialsException();
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        account.setPassword(encodedNewPassword);

        return save(account);
    }

    private Account save(Account account) {
        try {
            accountRepository.save(account);
            accountRepository.flush();
            return account;

        } catch (DataAccessException e) {
            var violationException = ExceptionUtil.findCause(e, ConstraintViolationException.class);

            if (Objects.nonNull(violationException) && Objects.nonNull(violationException.getConstraintName())) {
                return handleConstraintViolationException(violationException);
            }

            throw ApplicationExceptionFactory.createUnknownException();
        }
    }

    private Account handleConstraintViolationException(ConstraintViolationException e) {
        switch (e.getConstraintName()) {
            case "accounts_login_key" -> throw ApplicationExceptionFactory.createAccountLoginConflictException();
            case "accounts_email_key" -> throw ApplicationExceptionFactory.createAccountEmailConflictException();
            default -> throw SystemExceptionFactory.createDbConstraintViolationException(e);
        }
    }

    private void updatePersonalInformation(Account account, NewContactData personalInformation) {
        Contact contact = account.getContact();

        setNullableValue(personalInformation.firstName(), contact::setFirstName);
        setNullableValue(personalInformation.lastName(), contact::setLastName);
        setNullableValue(personalInformation.postalCode(), contact::setPostalCode);
        setNullableValue(personalInformation.country(), contact::setCountry);
        setNullableValue(personalInformation.city(), contact::setCity);
        setNullableValue(personalInformation.street(), contact::setStreet);
        setNullableValue(personalInformation.houseNumber(), contact::setHouseNumber);
    }

    private void archiveAccount(Account account) {
        Contact contact = account.getContact();
        account.setArchival(true);
        contact.setArchival(true);
        contact.getAddress().setArchival(true);
    }

}
