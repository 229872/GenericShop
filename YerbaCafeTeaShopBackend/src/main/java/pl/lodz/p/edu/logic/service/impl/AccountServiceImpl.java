package pl.lodz.p.edu.logic.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.edu.dataaccess.model.Account;
import pl.lodz.p.edu.dataaccess.model.sub.AccountRole;
import pl.lodz.p.edu.dataaccess.repository.api.AccountRepository;
import pl.lodz.p.edu.exception.ExceptionFactory;
import pl.lodz.p.edu.logic.service.api.AccountService;

import java.util.List;

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
        return null;
    }

    @Override
    public Account update(Long id, Account account) {
        return null;
    }

    @Override
    public Account block(Long id) {
        return null;
    }

    @Override
    public Account unblock(Long id) {
        return null;
    }

    @Override
    public Account archive(Long id) {
        return null;
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
}
