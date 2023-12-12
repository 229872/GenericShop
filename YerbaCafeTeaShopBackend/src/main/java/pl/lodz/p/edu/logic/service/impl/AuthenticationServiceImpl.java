package pl.lodz.p.edu.logic.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.edu.dataaccess.model.Account;
import pl.lodz.p.edu.dataaccess.model.sub.AccountState;
import pl.lodz.p.edu.dataaccess.repository.api.AccountRepository;
import pl.lodz.p.edu.exception.ExceptionFactory;
import pl.lodz.p.edu.logic.model.JwtTokens;
import pl.lodz.p.edu.logic.service.api.AuthenticationService;
import pl.lodz.p.edu.logic.service.api.JwtService;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j

@Service
@Transactional(transactionManager = "accountsModTxManager", propagation = Propagation.REQUIRES_NEW)
@Qualifier("AuthenticationServiceImpl")
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${app.auth.unsuccessful_attempts}")
    private Integer unsuccessfulAuthAttempts;

    @Value("${app.auth.blockade_time_in_minutes}")
    private Integer blockadeTimeInMinutes;

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final HttpServletRequest request;

    @Override
    @Transactional(transactionManager = "accountsModTxManager", noRollbackFor = ResponseStatusException.class)
    public JwtTokens authenticate(String login, String password) {
        Account account = accountRepository.findByLogin(login)
            .orElseThrow(ExceptionFactory::createInvalidCredentialsException);

        String ipAddress = Optional.ofNullable(request.getHeader("X-Forwarded-For")).orElse(request.getRemoteAddr());
        validateAccount(account, password, ipAddress);

        String logMessage = "User with account login: {} tried to authenticate from address: {} with status: SUCCESS";
        log.info(logMessage, account.getLogin(), ipAddress);

        account.setUnsuccessfulAuthCounter(0);
        account.setLastSuccessfulAuthTime(LocalDateTime.now());
        account.setLastSuccessfulAuthIpAddr(ipAddress);
        accountRepository.save(account);

        String jwtToken = jwtService.generateToken(account);
        String refreshToken = jwtService.generateRefreshToken(account);

        return JwtTokens.builder()
            .token(jwtToken)
            .refreshToken(refreshToken)
            .build();
    }

    @Override
    public JwtTokens getAuthenticationToken(String login, String refreshToken) {
        jwtService.validateRefreshToken(refreshToken);

        String loginFromRefreshToken = jwtService.getLoginFromRefreshToken(refreshToken);

        if (!login.equals(loginFromRefreshToken)) {
            throw ExceptionFactory.createInvalidRefreshTokenException();
        }

        Account account = accountRepository.findByLogin(login)
            .orElseThrow(ExceptionFactory::createAccountNotFoundException);

        String token = jwtService.generateToken(account);
        String newRefreshToken = jwtService.generateRefreshToken(account);

        return JwtTokens.builder()
            .token(token)
            .refreshToken(newRefreshToken)
            .build();
    }

    private void validateAccount(Account account, String password, String ipAddress) {
        try {
            if (!passwordEncoder.matches(password, account.getPassword())) {
                auditUnsuccessfulAuthenticationWhenCredentialsAreInvalid(account, ipAddress);
                throw ExceptionFactory.createInvalidCredentialsException();
            }

            if (account.isArchival()) {
                throw ExceptionFactory.createCantAccessArchivalAccountException();
            }

            if (account.getAccountState().equals(AccountState.NOT_VERIFIED)) {
                throw ExceptionFactory.createCantAccessNotVerifiedAccountException();
            }

            if (account.getAccountState().equals(AccountState.BLOCKED)) {
                throw ExceptionFactory.createCantAccessBlockedAccountException();
            }

        } catch (ResponseStatusException e) {
            String logMessage = "User with account login: {} tried to authenticate from address: {} with status: FAILURE";
            log.info(logMessage, account.getLogin(), ipAddress);

            if (account.getAccountState().equals(AccountState.BLOCKED)) {
                log.info("User with account login: {} has been blocked", account.getLogin());
            }
            throw e;
        }
    }

    private void auditUnsuccessfulAuthenticationWhenCredentialsAreInvalid(Account account, String ipAddress) {

        if (Objects.equals(account.getUnsuccessfulAuthCounter(), unsuccessfulAuthAttempts)) {

            account.setUnsuccessfulAuthCounter(0);
            account.setAccountState(AccountState.BLOCKED);
            account.setBlockadeEndTime(LocalDateTime.now().plusMinutes(blockadeTimeInMinutes));

            unblockAccountAfterTimeout(account);
        } else  {
            account.setUnsuccessfulAuthCounter(account.getUnsuccessfulAuthCounter() + 1);
        }

        account.setLastUnsuccessfulAuthTime(LocalDateTime.now());
        account.setLastUnsuccessfulAuthIpAddr(ipAddress);
        accountRepository.save(account);
    }

    private void unblockAccountAfterTimeout(Account account) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                accountRepository.findById(account.getId())
                    .ifPresent(a -> {
                        if (account.getAccountState().equals(AccountState.BLOCKED) &&
                            account.getBlockadeEndTime() != null) {

                            account.setAccountState(AccountState.ACTIVE);
                            account.setBlockadeEndTime(null);

                            accountRepository.save(account);
                            log.info("Account with login: {} has been unblocked after timeout", a.getLogin());
                        }
                    });
            }
        }, TimeUnit.MINUTES.toMillis(blockadeTimeInMinutes));
    }
}
