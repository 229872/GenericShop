package pl.lodz.p.edu.shop.logic.service.impl;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.lodz.p.edu.shop.AccountsModuleTestData;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountState;
import pl.lodz.p.edu.shop.dataaccess.repository.api.AccountRepository;
import pl.lodz.p.edu.shop.exception.ApplicationExceptionFactory;
import pl.lodz.p.edu.shop.exception.ExceptionMessage;
import pl.lodz.p.edu.shop.exception.auth.*;
import pl.lodz.p.edu.shop.logic.model.JwtTokens;
import pl.lodz.p.edu.shop.logic.service.api.JwtService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static pl.lodz.p.edu.shop.AccountsModuleTestData.getDefaultAccountBuilder;

@DisplayName("Unit tests for AuthenticationServiceImpl")
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthenticationServiceImpl underTest;

    @AfterEach
    void tearDown() {
        AccountsModuleTestData.resetCounter();
    }

    @Nested @DisplayName("authenticate tests")
    class AuthenticateTests {

        @Test
        @DisplayName("Should authenticate successfully when found account is not archival and is active ")
        void shouldAuthenticateSuccessfully() {
            //given
            String login = "test_user";
            String password = "password";
            Account givenAccount = createActiveAccount(login);

            given(accountRepository.findByLogin(login)).willReturn(Optional.of(givenAccount));
            given(passwordEncoder.matches(password, givenAccount.getPassword())).willReturn(true);
            given(jwtService.generateAuthToken(givenAccount)).willReturn("jwt-token");
            given(jwtService.generateRefreshToken(login)).willReturn("refresh-token");

            //when
            JwtTokens tokens = underTest.authenticate(login, password);

            //then
            assertNotNull(tokens);
            assertEquals("jwt-token", tokens.token());
            assertEquals("refresh-token", tokens.refreshToken());
        }

        @Test
        @DisplayName("Should throw InvalidCredentialsException when account can't be found")
        void shouldThrowIfAccountCantBeFound() {
            //given
            var givenLogin = "test_user";
            given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.empty());

            //when
            var result = catchException(() -> underTest.authenticate(givenLogin, "password"));

            //then
            assertThat(result)
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessageContaining(ExceptionMessage.INVALID_CREDENTIALS);
        }

        @Test
        @DisplayName("Should throw InvalidCredentialsException when credentials aren't correct")
        void shouldThrowIfCredentialsArentCorrect() {
            //given
            String login = "test_user";
            String password = "wrong_password";
            var givenAccount = createActiveAccount(login);

            given(accountRepository.findByLogin(login)).willReturn(Optional.of(givenAccount));
            given(passwordEncoder.matches(password, givenAccount.getPassword())).willReturn(false);

            //when
            var result = catchException(() -> underTest.authenticate(login, password));

            //then
            assertThat(result)
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessageContaining(ExceptionMessage.INVALID_CREDENTIALS);
        }

        @Test
        @DisplayName("Should throw CantAccessBlockedAccountException when found account is blocked")
        void shouldFailAuthenticationDueToBlockedAccount() {
            //given
            String login = "test_user";
            String password = "password";
            Account account = createActiveAccount(login);
            account.setAccountState(AccountState.BLOCKED);

            given(accountRepository.findByLogin(login)).willReturn(Optional.of(account));
            given(passwordEncoder.matches(password, account.getPassword())).willReturn(true);

            //when
            var result = catchException(() -> underTest.authenticate(login, password));

            //then
            assertThat(result)
                    .isInstanceOf(CantAccessBlockedAccountException.class)
                    .hasMessageContaining(ExceptionMessage.AUTH_ACCOUNT_BLOCKED);
        }

        @Test
        @DisplayName("Should throw CantAccessNotVerifiedAccountException when found account is blocked")
        void shouldFailAuthenticationDueToNotVerifiedAccount() {
            //given
            String login = "test_user";
            String password = "password";
            Account account = createActiveAccount(login);
            account.setAccountState(AccountState.NOT_VERIFIED);

            given(accountRepository.findByLogin(login)).willReturn(Optional.of(account));
            given(passwordEncoder.matches(password, account.getPassword())).willReturn(true);

            //when
            var result = catchException(() -> underTest.authenticate(login, password));

            //then
            assertThat(result)
                    .isInstanceOf(CantAccessNotVerifiedAccountException.class)
                    .hasMessageContaining(ExceptionMessage.AUTH_ACCOUNT_NOT_VERIFIED);
        }

        @Test
        @DisplayName("Should throw CantAccessNotVerifiedAccountException when found account is archival")
        void shouldFailAuthenticationDueToArchivalAccount() {
            //given
            String login = "test_user";
            String password = "password";
            Account account = createActiveAccount(login);
            account.setArchival(true);

            given(accountRepository.findByLogin(login)).willReturn(Optional.of(account));
            given(passwordEncoder.matches(password, account.getPassword())).willReturn(true);

            //when
            var result = catchException(() -> underTest.authenticate(login, password));

            //then
            assertThat(result)
                    .isExactlyInstanceOf(CantAccessArchivalAccountException.class)
                    .hasMessageContaining(ExceptionMessage.AUTH_ACCOUNT_ARCHIVAL);
        }
    }

    @Nested @DisplayName("Extend session tests")
    class ExtendSessionTests {

        @Test
        @DisplayName("Should extend session successfully when given valid refresh token and login")
        void shouldExtendSessionSuccessfully() {
            //given
            String login = "test_user";
            String refreshToken = "valid_refresh_token";
            Account account = createActiveAccount(login);
            var claims = mock(Claims.class);

            given(jwtService.validateAndExtractClaimsFromRefreshToken(refreshToken)).willReturn(claims);
            given(claims.getSubject()).willReturn(login);
            given(accountRepository.findByLogin(login)).willReturn(Optional.of(account));
            given(jwtService.generateAuthToken(account)).willReturn("new_jwt_token");

            //when
            JwtTokens result = underTest.extendSession(login, refreshToken);

            //then
            assertNotNull(result);
            assertEquals("new_jwt_token", result.token());
            assertEquals(refreshToken, result.refreshToken());
        }

        @Test
        @DisplayName("Should throw InvalidTokenException when refresh token is invalid")
        void shouldThrowInvalidTokenExceptionForInvalidToken() {
            //given
            String login = "test_user";
            String refreshToken = "invalid_refresh_token";

            given(jwtService.validateAndExtractClaimsFromRefreshToken(refreshToken))
                    .willThrow(ApplicationExceptionFactory.createInvalidTokenException());

            //when
            var result = catchException(() -> underTest.extendSession(login, refreshToken));

            //then
            assertThat(result)
                    .isExactlyInstanceOf(InvalidTokenException.class)
                    .hasMessageContaining(ExceptionMessage.TOKEN_INVALID);
        }

        @Test
        @DisplayName("Should throw InvalidTokenException when login in refresh token does not match provided login")
        void shouldThrowInvalidTokenExceptionForMismatchedLogin() {
            //given
            String login = "expected_login";
            String refreshToken = "valid_refresh_token";
            String mismatchedLogin = "other_login";
            var claims = mock(Claims.class);

            given(jwtService.validateAndExtractClaimsFromRefreshToken(refreshToken))
                    .willReturn(claims);
            given(claims.getSubject()).willReturn(mismatchedLogin);

            //when
            var result = catchException(() -> underTest.extendSession(login, refreshToken));

            //then
            assertThat(result)
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessageContaining(ExceptionMessage.TOKEN_INVALID);
        }

        @Test
        @DisplayName("Should throw AccountNotFoundException when account is not found")
        void shouldThrowAccountNotFoundExceptionWhenAccountNotFound() {
            //given
            String login = "test_user";
            String refreshToken = "valid_refresh_token";
            var claims = mock(Claims.class);

            given(jwtService.validateAndExtractClaimsFromRefreshToken(refreshToken)).willReturn(claims);
            given(accountRepository.findByLogin(login)).willReturn(Optional.empty());
            given(claims.getSubject()).willReturn(login);

            //when
            var result = catchException(() -> underTest.extendSession(login, refreshToken));

            //then
            assertThat(result)
                    .isInstanceOf(Exception.class)
                    .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_FOUND);
        }
    }


    private Account createActiveAccount(String login) {
        return getDefaultAccountBuilder()
                .login(login)
                .accountState(AccountState.ACTIVE)
                .build();
    }
}
