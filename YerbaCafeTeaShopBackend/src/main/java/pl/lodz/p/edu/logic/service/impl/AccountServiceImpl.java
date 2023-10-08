package pl.lodz.p.edu.logic.service.impl;

import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.edu.dataaccess.model.Account;
import pl.lodz.p.edu.dataaccess.model.Address;
import pl.lodz.p.edu.dataaccess.model.Person;
import pl.lodz.p.edu.dataaccess.model.sub.AccountRole;
import pl.lodz.p.edu.dataaccess.model.sub.AccountState;
import pl.lodz.p.edu.dataaccess.repository.api.AccountRepository;
import pl.lodz.p.edu.exception.ExceptionFactory;
import pl.lodz.p.edu.logic.model.NewPersonalInformation;
import pl.lodz.p.edu.logic.service.api.AccountService;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
@Qualifier("AccountServiceImpl")
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

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
            .orElseThrow(ExceptionFactory::createAccountNotFoundException);
    }

    @Override
    public Account findByLogin(String login) {
        return accountRepository.findByLogin(login)
            .orElseThrow(ExceptionFactory::createAccountNotFoundException);
    }

    @Override
    public Account create(Account account) {
        return save(account);
    }

    @Override
    public Account updatePersonalInformation(Long id, NewPersonalInformation newPersonalInformation) {
        Account account = accountRepository.findById(id)
            .orElseThrow(ExceptionFactory::createAccountNotFoundException);

        updatePersonalInformation(account, newPersonalInformation);
        return save(account);
    }

    @Override
    public Account block(Long id) {
        Account account = accountRepository.findById(id)
            .orElseThrow(ExceptionFactory::createAccountNotFoundException);

        if (!account.getAccountState().equals(AccountState.ACTIVE)) {
            throw ExceptionFactory.createAccountNotActiveException();
        }

        account.setAccountState(AccountState.BLOCKED);
        return save(account);
    }

    @Override
    public Account unblock(Long id) {
        Account account = accountRepository.findById(id)
            .orElseThrow(ExceptionFactory::createAccountNotFoundException);

        if (!account.getAccountState().equals(AccountState.BLOCKED)) {
            throw ExceptionFactory.createAccountNotBlockedException();
        }

        account.setAccountState(AccountState.ACTIVE);
        return save(account);
    }

    @Override
    public Account archive(Long id) {
        Account account = accountRepository.findById(id)
            .orElseThrow(ExceptionFactory::createAccountNotFoundException);

        if (account.getAccountState().equals(AccountState.ARCHIVAL)) {
            throw ExceptionFactory.createAccountAlreadyArchivalException();
        }

        account.setAccountState(AccountState.ARCHIVAL);
        return save(account);
    }

    @Override
    public Account addRole(Long id, AccountRole newRole) {
        return null;
    }

    @Override
    public Account removeRole(Long id, AccountRole roleForRemoval) {
        return null;
    }

    @Override
    public Account changeRole(Long id, AccountRole oldRole, AccountRole newRole) {
        return null;
    }

    @Override
    public Account updateLocale(Long id, Locale locale) {
        return null;
    }

    private Account save(Account account) {
        try {
            accountRepository.save(account);
            accountRepository.flush();
            return account;
        } catch (DataIntegrityViolationException e) {
            return handleDataIntegrityViolationException(e);
        }
    }

    private Account handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        Throwable cause = e.getCause();
        if (cause instanceof ConstraintViolationException ex) {
            switch (ex.getConstraintName()) {
                case "accounts_login_key" -> throw ExceptionFactory.createAccountLoginConflictException();
                case "accounts_email_key" -> throw ExceptionFactory.createAccountEmailConflictException();
            }
        }
        throw ExceptionFactory.createUnknownException();
    }

    private void updatePersonalInformation(Account account, NewPersonalInformation personalInformation) {
        Person person = account.getPerson();
        Address address = person.getAddress();

        Optional.ofNullable(personalInformation.firstName()).ifPresent(person::setFirstName);
        Optional.ofNullable(personalInformation.lastName()).ifPresent(person::setLastName);
        Optional.ofNullable(personalInformation.postalCode()).ifPresent(address::setPostalCode);
        Optional.ofNullable(personalInformation.country()).ifPresent(address::setCountry);
        Optional.ofNullable(personalInformation.city()).ifPresent(address::setCity);
        Optional.ofNullable(personalInformation.street()).ifPresent(address::setStreet);
        Optional.ofNullable(personalInformation.houseNumber()).ifPresent(address::setHouseNumber);
    }

}