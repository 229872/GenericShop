package pl.lodz.p.edu.shop.logic.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.edu.shop.TestData;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.repository.api.AccountRepository;
import pl.lodz.p.edu.shop.exception.ExceptionMessage;
import pl.lodz.p.edu.shop.exception.account.AccountNotFoundException;
import pl.lodz.p.edu.shop.exception.auth.InvalidCredentialsException;

import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("Unit tests for AccountAccessServiceImpl")
@ExtendWith(MockitoExtension.class)
class AccountAccessServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountAccessServiceImpl underTest;

    @AfterEach
    void tearDown() {
        TestData.resetCounter();
    }


    @Test
    @DisplayName("Should return account if account with login is found")
    void findByLogin_positive_1() {
        //given
        String givenLogin = "login";
        Account givenAccount = TestData.buildDefaultAccount();
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
        Account givenAccount = TestData.getDefaultAccountBuilder()
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
        Account givenAccount = TestData.buildDefaultAccount();
        String givenLogin = givenAccount.getLogin();
        String givenPassword = givenAccount.getPassword();
        String newPassword = "newPassword123";

        given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.of(givenAccount));
        given(passwordEncoder.matches(TestData.defaultPassword, givenPassword)).willReturn(true);
        given(passwordEncoder.encode(newPassword)).willReturn(newPassword);
        given(accountRepository.save(givenAccount)).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        Account result = underTest.changePassword(givenLogin, TestData.defaultPassword, newPassword);

        //then
        then(accountRepository).should().findByLogin(givenLogin);
        then(passwordEncoder).should().matches(TestData.defaultPassword, givenPassword);
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
    @DisplayName("Should throw InvalidCredentialsException when credentials missmatch")
    void changePassword_negative_2() {
        //given
        Account givenAccount = TestData.buildDefaultAccount();
        String givenLogin = givenAccount.getLogin();
        String givenPassword = givenAccount.getPassword();
        String newPassword = "newPassword123";

        given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.of(givenAccount));
        given(passwordEncoder.matches(TestData.defaultPassword, givenPassword)).willReturn(false);

        //when
        Exception exception = catchException(() -> underTest.changePassword(givenLogin, TestData.defaultPassword, newPassword));

        //then
        then(accountRepository).should().findByLogin(givenLogin);
        then(passwordEncoder).should().matches(TestData.defaultPassword, givenPassword);
        then(passwordEncoder).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isInstanceOf(ResponseStatusException.class)
            .isExactlyInstanceOf(InvalidCredentialsException.class)
            .hasMessageContaining(ExceptionMessage.INVALID_CREDENTIALS);
    }
}