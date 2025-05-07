package pl.lodz.p.edu.shop.logic.service.impl;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.*;
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
import pl.lodz.p.edu.shop.exception.account.*;
import pl.lodz.p.edu.shop.exception.auth.ExpiredTokenException;
import pl.lodz.p.edu.shop.exception.auth.InvalidCredentialsException;
import pl.lodz.p.edu.shop.exception.other.ApplicationOptimisticLockException;
import pl.lodz.p.edu.shop.exception.other.DecodeException;
import pl.lodz.p.edu.shop.logic.service.api.JwtService;
import pl.lodz.p.edu.shop.logic.service.api.MailService;
import pl.lodz.p.edu.shop.logic.service.api.VersionSignatureVerifier;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static pl.lodz.p.edu.shop.AccountsModuleTestData.*;

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

    @Mock
    private VersionSignatureVerifier verifier;

    @InjectMocks
    private AccountAccessServiceImpl underTest;

    @AfterEach
    void tearDown() {
        AccountsModuleTestData.resetCounter();
    }



    @Nested @DisplayName("findByLogin tests")
    class FindByLoginTests {

        @Test
        @DisplayName("Should return account if account with login is found")
        void findByLogin_positive_1() {
            //given
            String givenLogin = "login";
            Account givenAccount = buildDefaultAccount();
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
    }

    @Nested @DisplayName("updateOwnLocale tests")
    class UpdateOwnLocaleTests {

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

    }

    @Nested @DisplayName("changePassword tests")
    class ChangePasswordTests {

        @Test
        @DisplayName("Should change password if account can be found and current password matches")
        void changePassword_positive_1() {
            //given
            Account givenAccount = buildDefaultAccount();
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
            Account givenAccount = buildDefaultAccount();
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
    }

    @Nested @DisplayName("register tests")
    class RegisterTests {

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
            Account givenAccount = buildDefaultAccount();
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
            Account givenAccount = buildDefaultAccount();
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

    @Nested @DisplayName("changeEmail tests")
    class ChangeEmailTests {

        @Test
        @DisplayName("Should throw AccountNotFoundException, when account can't be found")
        void shouldThrowWhenAccountNotFound() {
            //given
            var givenAccount = buildDefaultAccount();
            var givenLogin = givenAccount.getLogin();
            var givenNewEmail = "<EMAIL>";

            given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.empty());
            //when

            var result = catchException(() -> underTest.changeEmail(givenLogin, givenNewEmail));

            //then
            assertThat(result)
                    .isInstanceOf(AccountNotFoundException.class)
                    .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_FOUND);
        }

        @Test
        @DisplayName("Should throw CantModifyArchivalAccountException, when account is archival")
        void shouldThrowWhenAccountIsArchival() {
            //given
            var givenAccount = buildDefaultAccount();
            givenAccount.setArchival(true);
            var givenLogin = givenAccount.getLogin();
            var givenNewEmail = "<EMAIL>";

            given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.of(givenAccount));
            //when

            var result = catchException(() -> underTest.changeEmail(givenLogin, givenNewEmail));

            //then
            assertThat(result)
                    .isInstanceOf(CantModifyArchivalAccountException.class)
                    .hasMessageContaining(ExceptionMessage.ACCOUNT_ARCHIVAL);
        }

        @Test
        @DisplayName("Should change email when found account is not archival")
        void shouldChangeEmail() {
            //given
            var givenAccount = buildDefaultAccount();
            var givenLogin = givenAccount.getLogin();
            var oldEmail = givenAccount.getEmail();
            var givenNewEmail = "<EMAIL>";

            given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.of(givenAccount));
            //when

            var result = underTest.changeEmail(givenLogin, givenNewEmail);

            //then
            assertThat(result.getEmail())
                    .isEqualTo(givenNewEmail)
                    .isNotEqualTo(oldEmail);
        }
    }

    @Nested @DisplayName("updateContactInformation tests")
    class UpdateContactInformationTests {

        @Test
        @DisplayName("Should throw AccountNotFoundException when account can't be found")
        void shouldThrowWhenAccountCantBeFound() {
            //given
            var givenLogin = "newLogin";
            var newContact = buildDefaultContact();
            var givenFrontendVersion = "1.0.0";

            given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.empty());

            //when
            var result = catchException(() -> underTest.updateContactInformation(givenLogin, newContact, givenFrontendVersion));

            //then
            assertThat(result)
                    .isInstanceOf(AccountNotFoundException.class)
                    .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_FOUND);
        }

        @Test
        @DisplayName("Should throw CantModifyArchivalAccountException when account is archival")
        void shouldThrowWhenAccountIsArchival() {
            //given
            var givenAccount = buildDefaultAccount();
            givenAccount.setArchival(true);
            var givenLogin = givenAccount.getLogin();
            var newContact = buildDefaultContact();
            var givenFrontendVersion = "1.0.0";

            given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.of(givenAccount));

            //when
            var result = catchException(() -> underTest.updateContactInformation(givenLogin, newContact, givenFrontendVersion));

            //then
            assertThat(result)
                    .isInstanceOf(CantModifyArchivalAccountException.class)
                    .hasMessageContaining(ExceptionMessage.ACCOUNT_ARCHIVAL);
        }

        @Test
        @DisplayName("Should throw OptimisticLockException when frontend version doesn't match signature of entities versions")
        void shouldThrowOptimisticLockExceptionWhenVersionsMismatch() {
            //given
            var givenAddress = getDefaultAddressBuilder().version(1L).build();
            var givenContact = getDefaultContactBuilder().version(1L).address(givenAddress).build();
            var givenAccount = getDefaultAccountBuilder().version(1L).contact(givenContact).build();
            var givenLogin = givenAccount.getLogin();
            var newContact = buildDefaultContact();
            var givenFrontendVersion = "1.0.0";

            given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.of(givenAccount));
            given(verifier.verifySignature(anyLong(), anyString())).willReturn(false);

            //when
            var result = catchException(() -> underTest.updateContactInformation(givenLogin, newContact, givenFrontendVersion));

            //then
            assertThat(result)
                    .isInstanceOf(ApplicationOptimisticLockException.class)
                    .hasMessageContaining(ExceptionMessage.TRANSACTION_OPTIMISTIC_LOCK);
        }

        @Test
        @DisplayName("Should update contact information when found account isn't archival and versions match")
        void shouldUpdateContactInformation() {
            //given
            var givenAddress = getDefaultAddressBuilder().version(1L).build();
            var givenContact = getDefaultContactBuilder().version(1L).address(givenAddress).build();
            var givenAccount = getDefaultAccountBuilder().version(1L).contact(givenContact).build();
            var givenLogin = givenAccount.getLogin();
            var newContact = getDefaultContactBuilder().firstName("newFirstName").build();
            var givenFrontendVersion = "1.0.0";

            given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.of(givenAccount));
            given(verifier.verifySignature(anyLong(), anyString())).willReturn(true);

            //when
            var result = underTest.updateContactInformation(givenLogin, newContact, givenFrontendVersion);

            //then
            assertThat(result.getContact())
                    .isEqualTo(newContact);
        }
    }

    @Nested @DisplayName("confirmRegistration tests")
    class ConfirmRegistrationTests {

        @Test
        @DisplayName("Should throw DecodeException when token can't be decoded")
        void shouldThrowWhenTokenCantBeDecoded() {
            //given
            var givenToken = "token";
            given(jwtService.decodeSubjectFromJwtTokenWithoutValidation(anyString()))
                    .willThrow(DecodeException.class);

            //when
            var exception = catchException(() -> underTest.confirmRegistration(givenToken));

            //then
            assertThat(exception)
                    .isInstanceOf(DecodeException.class);
        }

        @Test
        @DisplayName("Should throw ExpiredTokenException when account can't be found")
        void shouldThrowWhenAccountCantBeFound() {
            //given
            var givenToken = "token";
            var givenLogin = "login";
            given(jwtService.decodeSubjectFromJwtTokenWithoutValidation(anyString()))
                    .willReturn(givenLogin);
            given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.empty());

            //when
            var exception = catchException(() -> underTest.confirmRegistration(givenToken));

            //then
            assertThat(exception)
                    .isInstanceOf(ExpiredTokenException.class)
                    .hasMessageContaining(ExceptionMessage.TOKEN_EXPIRED);
        }

        @Test
        @DisplayName("Should confirm registration setting Active status and using save method when no exception is thrown")
        void shouldConfirmRegistration() {
            //given
            var givenAccount = buildDefaultAccount();
            givenAccount.setAccountState(AccountState.NOT_VERIFIED);
            var givenLogin = givenAccount.getLogin();
            var givenToken = "token";
            given(jwtService.decodeSubjectFromJwtTokenWithoutValidation(anyString()))
                    .willReturn(givenLogin);
            given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.of(givenAccount));

            //when
            underTest.confirmRegistration(givenToken);

            //then
            assertThat(givenAccount.getAccountState())
                    .isEqualTo(AccountState.ACTIVE);

            verify(accountRepository).save(givenAccount);
        }
    }

    @Nested @DisplayName("forgotPassword tests")
    class ForgotPasswordTests {

        @Test
        @DisplayName("Should send reset password email when account with given email is found")
        void shouldSendResetPasswordEmail() {
            // given
            String givenEmail = "test@example.com";
            Account givenAccount = buildDefaultAccount();

            given(accountRepository.findByEmail(givenEmail)).willReturn(Optional.of(givenAccount));
            given(jwtService.generateResetPasswordToken(givenAccount.getLogin(), givenAccount.getPassword()))
                    .willReturn("reset-password-token");

            // when
            underTest.forgotPassword(givenEmail);

            // then
            then(accountRepository).should().findByEmail(givenEmail);
            then(jwtService).should().generateResetPasswordToken(givenAccount.getLogin(), givenAccount.getPassword());
            then(mailService).should().sendResetPasswordMail(givenEmail, givenAccount.getLocale(), "reset-password-token");
        }

        @Test
        @DisplayName("Should do nothing when no account is found for the given email")
        void shouldDoNothingWhenAccountNotFound() {
            // given
            String givenEmail = "nonexistent@example.com";

            given(accountRepository.findByEmail(givenEmail)).willReturn(Optional.empty());

            // when
            underTest.forgotPassword(givenEmail);

            // then
            then(accountRepository).should().findByEmail(givenEmail);
            then(jwtService).shouldHaveNoInteractions();
            then(mailService).shouldHaveNoInteractions();
        }
    }

    @Nested @DisplayName("validateResetPasswordToken tests")
    class ValidateResetPasswordTokenTests {

        @Test
        @DisplayName("Should successfully validate token when no exception is thrown")
        void shouldValidateTokenSuccessfully() {
            //given
            String givenToken = "validToken";
            Account givenAccount = buildDefaultAccount();

            given(jwtService.decodeSubjectFromJwtTokenWithoutValidation(givenToken)).willReturn(givenAccount.getLogin());
            given(accountRepository.findByLogin(givenAccount.getLogin())).willReturn(Optional.of(givenAccount));

            //when
            underTest.validateResetPasswordToken(givenToken);

            //then
            then(jwtService).should().decodeSubjectFromJwtTokenWithoutValidation(givenToken);
            then(accountRepository).should().findByLogin(givenAccount.getLogin());
            then(jwtService).should().validateResetPasswordToken(givenToken, givenAccount.getPassword());
        }

        @Test
        @DisplayName("Should throw DecodeException when token decoding fails")
        void shouldThrowDecodeExceptionForInvalidToken() {
            //given
            String invalidToken = "invalidToken";

            given(jwtService.decodeSubjectFromJwtTokenWithoutValidation(invalidToken)).willThrow(DecodeException.class);

            //when
            Exception exception = catchException(() -> underTest.validateResetPasswordToken(invalidToken));

            //then
            then(jwtService).should().decodeSubjectFromJwtTokenWithoutValidation(invalidToken);
            then(accountRepository).shouldHaveNoInteractions();
            then(jwtService).shouldHaveNoMoreInteractions();

            assertThat(exception).isInstanceOf(DecodeException.class);
        }

        @Test
        @DisplayName("Should throw ExpiredTokenException when token has expired")
        void shouldThrowExpiredTokenException() {
            //given
            String expiredToken = "expiredToken";
            String givenLogin = "login";

            given(jwtService.decodeSubjectFromJwtTokenWithoutValidation(expiredToken)).willReturn(givenLogin);
            given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.empty());

            //when
            Exception exception = catchException(() -> underTest.validateResetPasswordToken(expiredToken));

            //then
            then(jwtService).should().decodeSubjectFromJwtTokenWithoutValidation(expiredToken);
            then(accountRepository).should().findByLogin(givenLogin);
            then(jwtService).shouldHaveNoMoreInteractions();

            assertThat(exception).isInstanceOf(ExpiredTokenException.class)
                    .hasMessageContaining(ExceptionMessage.TOKEN_EXPIRED);
        }

        @Test
        @DisplayName("Should throw OperationNotAllowedException when account is not active")
        void shouldThrowOperationNotAllowedExceptionForInactiveAccount() {
            //given
            String givenToken = "inactiveToken";
            Account givenAccount = buildDefaultAccount();
            givenAccount.setAccountState(AccountState.NOT_VERIFIED);

            given(jwtService.decodeSubjectFromJwtTokenWithoutValidation(givenToken)).willReturn(givenAccount.getLogin());
            given(accountRepository.findByLogin(givenAccount.getLogin())).willReturn(Optional.of(givenAccount));

            //when
            Exception exception = catchException(() -> underTest.validateResetPasswordToken(givenToken));

            //then
            then(jwtService).should().decodeSubjectFromJwtTokenWithoutValidation(givenToken);
            then(accountRepository).should().findByLogin(givenAccount.getLogin());
            then(jwtService).shouldHaveNoMoreInteractions();

            assertThat(exception).isInstanceOf(ResponseStatusException.class)
                    .isInstanceOf(OperationNotAllowedWithActualAccountStateException.class)
                    .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_ACTIVE);
        }

        @Test
        @DisplayName("Should throw CantModifyArchivalAccountException when account is archival")
        void shouldThrowCantModifyArchivalAccountExceptionForArchivalAccount() {
            //given
            String givenToken = "archivalToken";
            Account givenAccount = buildDefaultAccount();
            givenAccount.setArchival(true);

            given(jwtService.decodeSubjectFromJwtTokenWithoutValidation(givenToken)).willReturn(givenAccount.getLogin());
            given(accountRepository.findByLogin(givenAccount.getLogin())).willReturn(Optional.of(givenAccount));

            //when
            Exception exception = catchException(() -> underTest.validateResetPasswordToken(givenToken));

            //then
            then(jwtService).should().decodeSubjectFromJwtTokenWithoutValidation(givenToken);
            then(accountRepository).should().findByLogin(givenAccount.getLogin());
            then(jwtService).shouldHaveNoMoreInteractions();

            assertThat(exception).isInstanceOf(CantModifyArchivalAccountException.class)
                    .hasMessageContaining(ExceptionMessage.ACCOUNT_ARCHIVAL);
        }
    }

    @Nested
    @DisplayName("resetPassword tests")
    class ResetPasswordTests {

        @Test
        @DisplayName("Should reset password successfully when token is valid")
        void shouldResetPasswordSuccessfully() {
            //given
            String givenToken = "validToken";
            String newPassword = "newSecurePassword123";
            Account givenAccount = buildDefaultAccount();
            var givenPassword = givenAccount.getPassword();

            given(jwtService.decodeSubjectFromJwtTokenWithoutValidation(givenToken))
                    .willReturn(givenAccount.getLogin());
            given(accountRepository.findByLogin(givenAccount.getLogin()))
                    .willReturn(Optional.of(givenAccount));
            given(accountRepository.save(givenAccount))
                    .willAnswer(invocation -> invocation.getArgument(0));
            given(passwordEncoder.encode(newPassword)).willReturn(newPassword);

            //when
            underTest.resetPassword(newPassword, givenToken);

            //then
            assertThat(givenAccount.getPassword())
                    .isEqualTo(newPassword)
                    .isNotEqualTo(givenPassword);
        }

        @Test
        @DisplayName("Should throw DecodeException for invalid reset password token")
        void shouldThrowDecodeExceptionForInvalidToken() {
            //given
            String invalidToken = "invalidToken";

            given(jwtService.decodeSubjectFromJwtTokenWithoutValidation(invalidToken)).willThrow(DecodeException.class);

            //when
            Exception exception = catchException(() -> underTest.resetPassword("somePassword", invalidToken));

            //then
            then(jwtService).should().decodeSubjectFromJwtTokenWithoutValidation(invalidToken);
            then(accountRepository).shouldHaveNoInteractions();

            assertThat(exception).isInstanceOf(DecodeException.class);
        }

        @Test
        @DisplayName("Should throw ExpiredTokenException when account is not found for token")
        void shouldThrowExpiredTokenExceptionWhenAccountNotFound() {
            //given
            String expiredToken = "expiredToken";
            String givenLogin = "login";

            given(jwtService.decodeSubjectFromJwtTokenWithoutValidation(expiredToken)).willReturn(givenLogin);
            given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.empty());

            //when
            Exception exception = catchException(() -> underTest.resetPassword("somePassword", expiredToken));

            //then
            then(jwtService).should().decodeSubjectFromJwtTokenWithoutValidation(expiredToken);
            then(accountRepository).should().findByLogin(givenLogin);

            assertThat(exception).isInstanceOf(ExpiredTokenException.class)
                    .hasMessageContaining(ExceptionMessage.TOKEN_EXPIRED);
        }

        @Test
        @DisplayName("Should throw CantModifyArchivalAccountException when account is archival")
        void shouldThrowCantModifyArchivalAccountException() {
            //given
            String givenToken = "archivalToken";
            Account givenAccount = buildDefaultAccount();
            givenAccount.setArchival(true);

            given(jwtService.decodeSubjectFromJwtTokenWithoutValidation(givenToken)).willReturn(givenAccount.getLogin());
            given(accountRepository.findByLogin(givenAccount.getLogin())).willReturn(Optional.of(givenAccount));

            //when
            Exception exception = catchException(() -> underTest.resetPassword("newPassword", givenToken));

            //then
            then(jwtService).should().decodeSubjectFromJwtTokenWithoutValidation(givenToken);
            then(accountRepository).should().findByLogin(givenAccount.getLogin());

            assertThat(exception).isInstanceOf(CantModifyArchivalAccountException.class)
                    .hasMessageContaining(ExceptionMessage.ACCOUNT_ARCHIVAL);
        }
    }
}