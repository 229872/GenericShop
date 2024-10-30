package pl.lodz.p.edu.shop.logic.service.impl;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.edu.shop.AccountsModuleTestData;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountRole;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountState;
import pl.lodz.p.edu.shop.dataaccess.repository.api.AccountRepository;
import pl.lodz.p.edu.shop.exception.ExceptionMessage;
import pl.lodz.p.edu.shop.exception.account.AccountEmailConflictException;
import pl.lodz.p.edu.shop.exception.account.AccountLoginConflictException;
import pl.lodz.p.edu.shop.exception.account.AccountNotFoundException;
import pl.lodz.p.edu.shop.exception.auth.InvalidCredentialsException;
import pl.lodz.p.edu.shop.logic.service.api.JwtService;
import pl.lodz.p.edu.shop.logic.service.api.MailService;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("Unit tests for AccountAccessServiceImpl")
@ExtendWith(MockitoExtension.class)
class AccountAccessServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailService mailService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AccountAccessServiceImpl underTest;

    @AfterEach
    void tearDown() {
        AccountsModuleTestData.resetCounter();
    }


    @Test
    @DisplayName("Should return account if account with login is found")
    void findByLogin_positive_1() {
        //given
        String givenLogin = "login";
        Account givenAccount = AccountsModuleTestData.buildDefaultAccount();
        given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.of(givenAccount));

        //when
        Account result = underTest.findByLogin(givenLogin);

        //then
        then(accountRepository).should().findByLogin(givenLogin);
        assertThat(result)
            .isEqualTo(givenAccount);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException if account with login is not found")
    void findByLogin_negative_1() {
        //given
        String givenLogin = "login";
        given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.empty());

        //when
        Exception exception = catchException(() -> underTest.findByLogin(givenLogin));

        //then
        then(accountRepository).should().findByLogin(givenLogin);
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

        given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.of(givenAccount));
        given(accountRepository.save(givenAccount)).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        Account result = underTest.updateOwnLocale(givenLogin, givenNewLocale);

        //then
        then(accountRepository).should().findByLogin(givenLogin);
        then(accountRepository).should().save(givenAccount);

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

        given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.empty());

        //when
        Exception exception = catchException(() -> underTest.updateOwnLocale(givenLogin, givenLocale));

        //then
        then(accountRepository).should().findByLogin(givenLogin);
        then(accountRepository).shouldHaveNoMoreInteractions();

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

        given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.of(givenAccount));
        given(passwordEncoder.matches(AccountsModuleTestData.defaultPassword, givenPassword)).willReturn(true);
        given(passwordEncoder.encode(newPassword)).willReturn(newPassword);
        given(accountRepository.save(givenAccount)).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        Account result = underTest.changePassword(givenLogin, AccountsModuleTestData.defaultPassword, newPassword);

        //then
        then(accountRepository).should().findByLogin(givenLogin);
        then(passwordEncoder).should().matches(AccountsModuleTestData.defaultPassword, givenPassword);
        then(passwordEncoder).should().encode(newPassword);
        then(accountRepository).should().save(givenAccount);

        assertThat(result.getPassword())
            .isNotEqualTo(givenPassword)
            .isEqualTo(newPassword);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account can't be found")
    void changePassword_negative_1() {
        //given
        String givenLogin = "login";

        given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.empty());

        //when
        Exception exception = catchException(() -> underTest.findByLogin(givenLogin));

        //then
        then(accountRepository).should().findByLogin(givenLogin);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isInstanceOf(ResponseStatusException.class)
            .isExactlyInstanceOf(AccountNotFoundException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when credentials mismatch")
    void changePassword_negative_2() {
        //given
        Account givenAccount = AccountsModuleTestData.buildDefaultAccount();
        String givenLogin = givenAccount.getLogin();
        String givenPassword = givenAccount.getPassword();
        String newPassword = "newPassword123";

        given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.of(givenAccount));
        given(passwordEncoder.matches(AccountsModuleTestData.defaultPassword, givenPassword)).willReturn(false);

        //when
        Exception exception = catchException(() -> underTest.changePassword(givenLogin, AccountsModuleTestData.defaultPassword, newPassword));

        //then
        then(accountRepository).should().findByLogin(givenLogin);
        then(passwordEncoder).should().matches(AccountsModuleTestData.defaultPassword, givenPassword);
        then(passwordEncoder).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isInstanceOf(ResponseStatusException.class)
            .isExactlyInstanceOf(InvalidCredentialsException.class)
            .hasMessageContaining(ExceptionMessage.INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("Should register new account with accountState NOT_VERIFIED and account role CLIENT")
    void register_positive_1() {
        //given
        HashSet<AccountRole> givenAccountRoles = new HashSet<>(Set.of(AccountRole.GUEST));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountState(null)
            .accountRoles(givenAccountRoles)
            .build();
        String givenLogin = givenAccount.getLogin();
        String givenEmail = givenAccount.getEmail();
        String givenToken = "token";

        given(jwtService.generateVerificationToken(givenLogin, givenEmail)).willReturn(givenToken);
        given(accountRepository.save(givenAccount)).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        Account result = underTest.register(givenAccount);

        //then
        then(jwtService).should().generateVerificationToken(givenLogin, givenEmail);
        then(mailService).should().sendVerificationMail(givenEmail, givenAccount.getLocale(), givenToken);
        then(accountRepository).should().save(givenAccount);

        Assertions.assertEquals(AccountState.NOT_VERIFIED, result.getAccountState());
        Assertions.assertEquals(Set.of(AccountRole.CLIENT), result.getAccountRoles());

        assertThat(result)
            .isNotNull()
            .isEqualTo(givenAccount);
    }

    @Test
    @DisplayName("Should throw AccountLoginConflictException when creating account with already used login")
    void register_negative_2() {
        //given
        Account givenAccount = AccountsModuleTestData.buildDefaultAccount();
        String givenLogin = givenAccount.getLogin();
        String givenEmail = givenAccount.getEmail();
        String givenToken = "token";

        var constraintViolationEx = new ConstraintViolationException("accounts_login_key", null, "accounts_login_key");
        var dataAccessEx = new DataIntegrityViolationException("Conflict", constraintViolationEx);

        given(jwtService.generateVerificationToken(givenLogin, givenEmail)).willReturn(givenToken);
        given(accountRepository.save(givenAccount)).willThrow(dataAccessEx);

        //when
        Exception exception = catchException(() -> underTest.register(givenAccount));

        //then
        then(jwtService).should().generateVerificationToken(givenLogin, givenEmail);
        then(mailService).should().sendVerificationMail(givenEmail, givenAccount.getLocale(), givenToken);
        then(accountRepository).should().save(givenAccount);

        assertThat(exception)
            .isNotNull()
            .isInstanceOf(ResponseStatusException.class)
            .isExactlyInstanceOf(AccountLoginConflictException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_CONFLICT_LOGIN);
    }

    @Test
    @DisplayName("Should throw AccountEmailConflictException when creating account with already used newEmail")
    void register_negative_3() {
        //given
        Account givenAccount = AccountsModuleTestData.buildDefaultAccount();
        String givenLogin = givenAccount.getLogin();
        String givenEmail = givenAccount.getEmail();
        String givenToken = "token";

        var constraintViolationEx = new ConstraintViolationException("accounts_email_key", null, "accounts_email_key");
        var dataAccessEx = new DataIntegrityViolationException("Conflict", constraintViolationEx);

        given(jwtService.generateVerificationToken(givenLogin, givenEmail)).willReturn(givenToken);
        given(accountRepository.save(givenAccount)).willThrow(dataAccessEx);

        //when
        Exception exception = catchException(() -> underTest.register(givenAccount));

        //then
        then(jwtService).should().generateVerificationToken(givenLogin, givenEmail);
        then(mailService).should().sendVerificationMail(givenEmail, givenAccount.getLocale(), givenToken);
        then(accountRepository).should().save(givenAccount);

        assertThat(exception)
            .isNotNull()
            .isInstanceOf(ResponseStatusException.class)
            .isExactlyInstanceOf(AccountEmailConflictException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_CONFLICT_EMAIL);
    }

}