package pl.lodz.p.edu.integration.service;

import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.edu.TestData;
import pl.lodz.p.edu.config.PostgresqlContainerSetup;
import pl.lodz.p.edu.dataaccess.model.Account;
import pl.lodz.p.edu.dataaccess.model.sub.AccountRole;
import pl.lodz.p.edu.dataaccess.model.sub.AccountState;
import pl.lodz.p.edu.exception.ExceptionMessage;
import pl.lodz.p.edu.exception.auth.CantAccessArchivalAccountException;
import pl.lodz.p.edu.exception.auth.CantAccessBlockedAccountException;
import pl.lodz.p.edu.exception.auth.CantAccessNotVerifiedAccountException;
import pl.lodz.p.edu.exception.auth.InvalidCredentialsException;
import pl.lodz.p.edu.logic.model.JwtTokens;
import pl.lodz.p.edu.logic.service.api.AuthenticationService;
import pl.lodz.p.edu.logic.service.api.JwtService;

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
    private PlatformTransactionManager txManager;

    @Autowired
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
            em.createQuery("DELETE FROM Person ").executeUpdate();
            em.createQuery("DELETE FROM Address ").executeUpdate();
            return status;
        });

        TestData.resetCounter();
    }

    @Test
    @DisplayName("Should return jwt token with account login in subject and roles in claims when account can be found and credentials are correct")
    void authenticate_should_return_jwt_token() {
        //given
        Account account = TestData.buildDefaultAccount();
        account.setAccountRoles(new HashSet<>(Set.of(AccountRole.CLIENT)));
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });

        //when
        JwtTokens result = underTest.authenticate(account.getLogin(), TestData.defaultPassword);

        //then
        assertThat(result)
            .isNotNull();

        Claims body = jwtService.parseJwt(result.token()).getBody();
        String login = body.getSubject();
        List<String> roles = body.get("roles", List.class);

        assertThat(login)
            .isNotNull()
            .isEqualTo(account.getLogin());

        assertThat(roles)
            .isEqualTo(account.getAccountRoles().stream().map(AccountRole::name).toList());
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when login is incorrect")
    void authenticate_should_throw_InvalidCredentialException_when_login_is_incorrect() {
        //given
        Account account = TestData.buildDefaultAccount();
        account.setAccountRoles(new HashSet<>(Set.of(AccountRole.CLIENT)));
        String givenWrongLogin = "wrongLogin";
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });

        //when
        Exception exception = catchException(() -> underTest.authenticate(givenWrongLogin, TestData.defaultPassword));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(InvalidCredentialsException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when password is incorrect")
    void authenticate_should_throw_InvalidCredentialException_when_password_is_incorrect() {
        //given
        Account account = TestData.buildDefaultAccount();
        account.setAccountRoles(new HashSet<>(Set.of(AccountRole.CLIENT)));
        String givenWrongPassword = "wrongPassword";
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });

        //when
        Exception exception = catchException(() -> underTest.authenticate(account.getLogin(), givenWrongPassword));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(InvalidCredentialsException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("Should throw CantAccessArchivalAccountException when account is archival")
    void authenticate_should_throw_CantAccessArchivalAccountException_when_account_is_archival() {
        //given
        Account account = TestData.buildDefaultAccount();
        account.setAccountRoles(new HashSet<>(Set.of(AccountRole.CLIENT)));
        txTemplate.execute(status -> {
            em.persist(account);
            account.setArchival(true);
            return status;
        });

        //when
        Exception exception = catchException(() -> underTest.authenticate(account.getLogin(), TestData.defaultPassword));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantAccessArchivalAccountException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.AUTH_ACCOUNT_ARCHIVAL);
    }

    @Test
    @DisplayName("Should throw CantAccessNotVerifiedAccountException when account is NOT_VERIFIED")
    void authenticate_should_throw_CantAccessNotVerifiedAccountException_when_account_is_NOT_VERIFIED() {
        //given
        Account account = TestData.buildDefaultAccount();
        account.setAccountRoles(new HashSet<>(Set.of(AccountRole.CLIENT)));
        account.setAccountState(AccountState.NOT_VERIFIED);

        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });

        //when
        Exception exception = catchException(() -> underTest.authenticate(account.getLogin(), TestData.defaultPassword));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantAccessNotVerifiedAccountException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.AUTH_ACCOUNT_NOT_VERIFIED);
    }

    @Test
    @DisplayName("Should throw CantAccessBlockedAccountException when account is BLOCKED")
    void authenticate_should_throw_CantAccessBlockedAccountException_when_account_is_BLOCKED() {
        //given
        Account account = TestData.buildDefaultAccount();
        account.setAccountRoles(new HashSet<>(Set.of(AccountRole.CLIENT)));
        account.setAccountState(AccountState.BLOCKED);

        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });

        //when
        Exception exception = catchException(() -> underTest.authenticate(account.getLogin(), TestData.defaultPassword));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantAccessBlockedAccountException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.AUTH_ACCOUNT_BLOCKED);
    }
}
