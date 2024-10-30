package pl.lodz.p.edu.shop.integration.service;

import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.edu.shop.AccountsModuleTestData;
import pl.lodz.p.edu.shop.config.PostgresqlContainerSetup;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountRole;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountState;
import pl.lodz.p.edu.shop.exception.ExceptionMessage;
import pl.lodz.p.edu.shop.exception.auth.CantAccessArchivalAccountException;
import pl.lodz.p.edu.shop.exception.auth.CantAccessBlockedAccountException;
import pl.lodz.p.edu.shop.exception.auth.CantAccessNotVerifiedAccountException;
import pl.lodz.p.edu.shop.exception.auth.InvalidCredentialsException;
import pl.lodz.p.edu.shop.logic.model.JwtTokens;
import pl.lodz.p.edu.shop.logic.service.api.AuthenticationService;
import pl.lodz.p.edu.shop.logic.service.api.JwtService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

@DisplayName("Integration tests for AuthenticationService")
@SpringBootTest
@ActiveProfiles("it")
public class AuthenticationServiceIT extends PostgresqlContainerSetup {

    @Autowired
    private AuthenticationService underTest;

    @Autowired
    private JwtService jwtService;

    @Autowired
    @Qualifier("accountsModTxManager")
    private PlatformTransactionManager txManager;

    @Autowired
    @Qualifier("accountsModEmFactory")
    private EntityManager em;

    private TransactionTemplate txTemplate;

    @BeforeEach
    void setUp() {
        txTemplate = new TransactionTemplate(txManager);
    }

    @AfterEach
    void tearDown() {
        txTemplate.execute(status -> {
            em.createQuery("DELETE FROM Account ").executeUpdate();
            em.createQuery("DELETE FROM Contact ").executeUpdate();
            em.createQuery("DELETE FROM Address ").executeUpdate();
            return status;
        });

        AccountsModuleTestData.resetCounter();
    }

    @Test
    @DisplayName("Should return jwt token with account login in subject and accountRoles in claims when account can be found and credentials are correct")
    @SuppressWarnings("unchecked")
    void authenticate_positive_1() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });

        //when
        JwtTokens result = underTest.authenticate(givenAccount.getLogin(), AccountsModuleTestData.defaultPassword);

        //then
        assertThat(result)
            .isNotNull();

        Claims body = jwtService.validateAndExtractClaimsFromAuthToken(result.token());
        String login = body.getSubject();
        List<String> roles = body.get("accountRoles", List.class);

        assertThat(login)
            .isNotNull()
            .isEqualTo(givenAccount.getLogin());

        assertThat(roles)
            .isEqualTo(givenAccount.getAccountRoles().stream().map(AccountRole::name).toList());
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when login is incorrect")
    void authenticate_negative_1() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        String givenWrongLogin = "wrongLogin";
        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });

        //when
        Exception exception = catchException(() -> underTest.authenticate(givenWrongLogin, AccountsModuleTestData.defaultPassword));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(InvalidCredentialsException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when password is incorrect")
    void authenticate_negative_2() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        String givenWrongPassword = "wrongPassword";
        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });

        //when
        Exception exception = catchException(() -> underTest.authenticate(givenAccount.getLogin(), givenWrongPassword));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(InvalidCredentialsException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("Should throw CantAccessArchivalAccountException when account is archival")
    void authenticate_negative_3() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            givenAccount.setArchival(true);
            return status;
        });

        //when
        Exception exception = catchException(() -> underTest.authenticate(givenAccount.getLogin(), AccountsModuleTestData.defaultPassword));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantAccessArchivalAccountException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.AUTH_ACCOUNT_ARCHIVAL);
    }

    @Test
    @DisplayName("Should throw CantAccessNotVerifiedAccountException when account is NOT_VERIFIED")
    void authenticate_negative_4() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .accountState(AccountState.NOT_VERIFIED)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });

        //when
        Exception exception = catchException(() -> underTest.authenticate(givenAccount.getLogin(), AccountsModuleTestData.defaultPassword));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantAccessNotVerifiedAccountException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.AUTH_ACCOUNT_NOT_VERIFIED);
    }

    @Test
    @DisplayName("Should throw CantAccessBlockedAccountException when account is BLOCKED")
    void authenticate_negative_5() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .accountState(AccountState.BLOCKED)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });

        //when
        Exception exception = catchException(() -> underTest.authenticate(givenAccount.getLogin(), AccountsModuleTestData.defaultPassword));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantAccessBlockedAccountException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.AUTH_ACCOUNT_BLOCKED);
    }
}
