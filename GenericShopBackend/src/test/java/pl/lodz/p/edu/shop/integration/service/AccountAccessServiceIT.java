package pl.lodz.p.edu.shop.integration.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.edu.shop.AccountsModuleTestData;
import pl.lodz.p.edu.shop.config.PostgresqlContainerSetup;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.exception.ExceptionMessage;
import pl.lodz.p.edu.shop.exception.account.AccountNotFoundException;
import pl.lodz.p.edu.shop.exception.auth.InvalidCredentialsException;
import pl.lodz.p.edu.shop.logic.service.api.AccountAccessService;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

@DisplayName("Integration tests for AccountAccessService")
@SpringBootTest
@ActiveProfiles("it")
public class AccountAccessServiceIT extends PostgresqlContainerSetup {

    @Autowired
    private AccountAccessService underTest;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    @DisplayName("Should return account if account with login is found")
    void findByLogin_positive_1() {
        //given
        Account givenAccount = AccountsModuleTestData.buildDefaultAccount();
        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });
        String givenLogin = givenAccount.getLogin();

        //when
        Account result = underTest.findByLogin(givenLogin);

        //then
        assertThat(result)
            .isEqualTo(givenAccount);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException if account with login is not found")
    void findByLogin_negative_1() {
        //given
        String givenLogin = "login";

        //when
        Exception exception = catchException(() -> underTest.findByLogin(givenLogin));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotFoundException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("Should update locale")
    void updateOwnLocale_positive_1() {
        //given
        String givenLanguage = "pl";
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .locale(givenLanguage)
            .build();
        String givenLogin = givenAccount.getLogin();
        Locale givenNewLocale = Locale.forLanguageTag("en");

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });

        //when
        Account result = underTest.updateOwnLocale(givenLogin, givenNewLocale);

        //then
        assertThat(result.getLocale())
            .isEqualTo(givenNewLocale.getLanguage())
            .isNotEqualTo(givenLanguage);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account with provided login can't be found")
    void updateOwnLocale_negative_1() {
        //given
        String givenLogin = "login";
        Locale givenLocale = Locale.forLanguageTag("en");

        //when
        Exception exception = catchException(() -> underTest.updateOwnLocale(givenLogin, givenLocale));

        //then
        assertThat(exception)
            .isNotNull()
            .isInstanceOf(ResponseStatusException.class)
            .isExactlyInstanceOf(AccountNotFoundException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("Should change password if account can be found and current password matches")
    void changePassword_positive_1() {
        //given
        Account givenAccount = AccountsModuleTestData.buildDefaultAccount();
        String givenLogin = givenAccount.getLogin();
        String givenPassword = givenAccount.getPassword();
        String newPassword = "newPassword123";

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });

        //when
        Account result = underTest.changePassword(givenLogin, AccountsModuleTestData.defaultPassword, newPassword);

        //then
        assertThat(result.getPassword())
            .isNotEqualTo(givenPassword);

        Assertions.assertTrue(passwordEncoder.matches(newPassword, result.getPassword()));
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account can't be found")
    void changePassword_negative_1() {
        //given
        String givenLogin = "login";
        String newPassword = "newPassword123";

        //when
        Exception exception = catchException(() -> underTest.changePassword(givenLogin, AccountsModuleTestData.defaultPassword, newPassword));

        //then
        assertThat(exception)
            .isNotNull()
            .isInstanceOf(ResponseStatusException.class)
            .isExactlyInstanceOf(AccountNotFoundException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when credentials missmatch")
    void changePassword_negative_2() {
        //given
        Account givenAccount = AccountsModuleTestData.buildDefaultAccount();
        String givenLogin = givenAccount.getLogin();
        String newPassword = "newPassword123";
        String wrongPassword = "wrongPassword";

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });

        //when
        Exception exception = catchException(() -> underTest.changePassword(givenLogin, wrongPassword, newPassword));

        //then
        assertThat(exception)
            .isNotNull()
            .isInstanceOf(ResponseStatusException.class)
            .isExactlyInstanceOf(InvalidCredentialsException.class)
            .hasMessageContaining(ExceptionMessage.INVALID_CREDENTIALS);
    }
}
