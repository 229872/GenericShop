package pl.lodz.p.edu.shop.logic.service.impl;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.edu.shop.AccountsModuleTestData;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountRole;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountState;
import pl.lodz.p.edu.shop.dataaccess.repository.api.AccountRepository;
import pl.lodz.p.edu.shop.exception.ExceptionMessage;
import pl.lodz.p.edu.shop.exception.account.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("Unit tests for AccountServiceImpl")
@ExtendWith(MockitoExtension.class)
class AccountManagementServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountManagementServiceImpl underTest;

    @AfterEach
    void tearDown() {
        AccountsModuleTestData.resetCounter();
    }

    @Test
    @DisplayName("Should return empty list")
    void findAll_positive_1() {
        //given
        given(accountRepository.findAll()).willReturn(new ArrayList<>());

        //when
        List<Account> result = underTest.findAll();

        //then
        then(accountRepository).should().findAll();
        assertThat(result)
            .isEmpty();
    }

    @Test
    @DisplayName("Should return list with elements")
    void findAll_positive_2() {
        //given
        Account[] accounts = {
            AccountsModuleTestData.buildDefaultAccount(),
            AccountsModuleTestData.buildDefaultAccount()
        };

        given(accountRepository.findAll()).willReturn(Arrays.stream(accounts).toList());

        //when
        List<Account> result = underTest.findAll();

        //then
        then(accountRepository).should().findAll();
        assertThat(result)
            .hasSize(2)
            .containsExactly(accounts);
    }


    @Test
    @DisplayName("Should return account if account with id is found")
    void findById_positive_1() {
        //given
        Long givenId = 1L;
        Account givenAccount = AccountsModuleTestData.buildDefaultAccount();
        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));

        //when
        Account result = underTest.findById(givenId);

        //then
        then(accountRepository).should().findById(givenId);
        assertThat(result)
            .isEqualTo(givenAccount);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException if account with id is not found")
    void findById_negative_1() {
        //given
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.empty());

        //when
        Exception exception = catchException(() -> underTest.findById(1L));

        //then
        then(accountRepository).should().findById(givenId);
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotFoundException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("Should create account")
    void create_positive_1() {
        //given
        Account givenAccount = AccountsModuleTestData.buildDefaultAccount();
        given(accountRepository.save(givenAccount)).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        Account result = underTest.create(givenAccount);

        //then
        then(accountRepository).should().save(givenAccount);
        assertThat(result)
            .isEqualTo(givenAccount);
    }

    @Test
    @DisplayName("Should throw CantCreateAccountWithManyRolesException when account has more than one role")
    void create_negative_1() {
        //given
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(new HashSet<>(Set.of(AccountRole.CLIENT, AccountRole.EMPLOYEE)))
            .build();

        //when
        Exception exception = catchException(() -> underTest.create(givenAccount));

        //then
        then(accountRepository).shouldHaveNoInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantCreateAccountWithManyRolesException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_CREATE_MANY_ROLES);
    }

    @Test
    @DisplayName("Should throw CantAssignGuestRoleException when account has guest role")
    void create_negative_2() {
        //given
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(new HashSet<>(Set.of(AccountRole.GUEST)))
            .build();

        //when
        Exception exception = catchException(() -> underTest.create(givenAccount));

        //then
        then(accountRepository).shouldHaveNoInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantAssignGuestRoleException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_ROLE_CANT_ASSIGN_GUEST);
    }

    @Test
    @DisplayName("Should throw CantCreateAccountWithNotVerifiedStatusException when account is not verified")
    void create_negative_3() {
        //given
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountState(AccountState.NOT_VERIFIED)
            .build();

        //when
        Exception exception = catchException(() -> underTest.create(givenAccount));

        //then
        then(accountRepository).shouldHaveNoInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantCreateAccountWithNotVerifiedStatusException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_CREATE_CANT_ASSIGN_NOT_VERIFIED);
    }

    @Test
    @DisplayName("Should throw AccountLoginConflictException when new Account has same login")
    void create_negative_4() {
        //given
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .login("login")
            .build();
        var cause = new ConstraintViolationException("Database violation occurred", null, "accounts_login_key");
        var dataIntegrityViolationException = new DataIntegrityViolationException("Violation occurred", cause);
        given(accountRepository.save(givenAccount)).willThrow(dataIntegrityViolationException);

        //when
        Exception exception = catchException(() -> underTest.create(givenAccount));

        //then
        then(accountRepository).should().save(givenAccount);
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountLoginConflictException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_CONFLICT_LOGIN);
    }

    @Test
    @DisplayName("Should throw AccountEmailConflictException when new Account has same newEmail")
    void create_negative_5() {
        //given
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .email("newEmail@example.com")
            .build();
        var cause = new ConstraintViolationException("Database violation occurred", null, "accounts_email_key");
        var dataIntegrityViolationException = new DataIntegrityViolationException("Violation occurred", cause);
        given(accountRepository.save(givenAccount)).willThrow(dataIntegrityViolationException);

        //when
        Exception exception = catchException(() -> underTest.create(givenAccount));

        //then
        then(accountRepository).should().save(givenAccount);
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountEmailConflictException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_CONFLICT_EMAIL);
    }

    @Test
    @DisplayName("Should block active account")
    void block_positive_1() {
        //given
        Long givenId = 1L;
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountState(AccountState.ACTIVE)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));
        given(accountRepository.save(givenAccount)).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        Account result = underTest.block(givenId);

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).should().save(givenAccount);

        assertEquals(AccountState.BLOCKED, result.getAccountState());
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account is not found")
    void block_negative_1() {
        //given
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.empty());

        //when
        Exception exception = catchException(() -> underTest.block(givenId));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotFoundException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("Should throw CantModifyArchivalAccountException when account is archival")
    void block_negative_2() {
        //given
        Long givenId = 1L;
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .isArchival(true)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));

        //when
        Exception exception = catchException(() -> underTest.block(givenId));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantModifyArchivalAccountException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_ARCHIVAL);
    }

    @Test
    @DisplayName("Should throw OperationNotAllowedWithActualAccountStateException when account is not verified")
    void block_negative_3() {
        //given
        Long givenId = 1L;
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountState(AccountState.NOT_VERIFIED)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));

        //when
        Exception exception = catchException(() -> underTest.block(givenId));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(OperationNotAllowedWithActualAccountStateException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_ACTIVE);
    }

    @Test
    @DisplayName("Should throw OperationNotAllowedWithActualAccountStateException when account is blocked")
    void block_negative_4() {
        //given
        Long givenId = 1L;
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountState(AccountState.BLOCKED)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));

        //when
        Exception exception = catchException(() -> underTest.block(givenId));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(OperationNotAllowedWithActualAccountStateException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_ACTIVE);
    }

    @Test
    @DisplayName("Should unblock blocked account")
    void unblock_positive_1() {
        //given
        Long givenId = 1L;
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountState(AccountState.BLOCKED)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));

        //when
        Account result = underTest.unblock(givenId);

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).should().save(givenAccount);

        assertEquals(AccountState.ACTIVE, result.getAccountState());
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account can't be found")
    void unblock_negative_1() {
        //given
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.empty());

        //when
        Exception exception = catchException(() -> underTest.unblock(givenId));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotFoundException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("Should throw CantModifyArchivalAccountException when account is archival")
    void unblock_negative_3() {
        //given
        Long givenId = 1L;
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .isArchival(true)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));

        //when
        Exception exception = catchException(() -> underTest.unblock(givenId));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantModifyArchivalAccountException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_ARCHIVAL);
    }

    @Test
    @DisplayName("Should throw OperationNotAllowedWithActualAccountStateException when account is not verified")
    void unblock_negative_4() {
        //given
        Long givenId = 1L;
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountState(AccountState.NOT_VERIFIED)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));

        //when
        Exception exception = catchException(() -> underTest.unblock(givenId));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(OperationNotAllowedWithActualAccountStateException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_BLOCKED);
    }

    @Test
    @DisplayName("Should throw OperationNotAllowedWithActualAccountStateException when account is active")
    void unblock_negative_5() {
        //given
        Long givenId = 1L;
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountState(AccountState.ACTIVE)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));

        //when
        Exception exception = catchException(() -> underTest.unblock(givenId));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(OperationNotAllowedWithActualAccountStateException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_BLOCKED);
    }

    @Test
    @DisplayName("Should archive active account")
    void archive_positive_1() {
        //given
        Long givenId = 1L;
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .isArchival(false)
            .accountState(AccountState.ACTIVE)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));
        given(accountRepository.save(givenAccount)).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        Account result = underTest.archive(givenId);

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).should().save(givenAccount);

        assertTrue(result.isArchival());
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account can't be found during archive")
    void archive_negative_1() {
        //given
        Long givenId = 1L;

        given(accountRepository.findById(givenId)).willReturn(Optional.empty());

        //when
        Exception exception = catchException(() -> underTest.archive(givenId));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotFoundException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("Should throw CantModifyArchivalAccountException when account is already archival")
    void archive_negative_2() {
        //given
        Long givenId = 1L;
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .isArchival(true)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));

        //when
        Exception exception = catchException(() -> underTest.archive(givenId));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantModifyArchivalAccountException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_ARCHIVAL);
    }

    @Test
    @DisplayName("Should add role when account with provided id is found")
    void addRole_positive_1() {
        //given
        Long givenId = 1L;
        Set<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account accoungivenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(accoungivenAccount));
        given(accountRepository.save(accoungivenAccount)).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        Account result = underTest.addRole(givenId, AccountRole.EMPLOYEE);

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).should().save(accoungivenAccount);

        assertThat(result.getAccountRoles())
            .hasSize(2)
            .contains(AccountRole.CLIENT, AccountRole.EMPLOYEE);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account can't be found")
    void addRole_negative_1() {
        //given
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.empty());

        //when
        Exception exception = catchException(() -> underTest.addRole(givenId, AccountRole.EMPLOYEE));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotFoundException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("Should throw CantModifyArchivalAccountException when account is archival")
    void addRole_negative_2() {
        //given
        Long givenId = 1L;
        Set<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .isArchival(true)
            .accountRoles(givenRoles)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));

        //when
        Exception exception = catchException(() -> underTest.addRole(givenId, AccountRole.EMPLOYEE));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantModifyArchivalAccountException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_ARCHIVAL);
    }

    @Test
    @DisplayName("Should throw AccountRoleAlreadyAssignedException when account already has this role assigned")
    void addRole_negative_3() {
        //given
        Long givenId = 1L;
        Set<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));

        //when
        Exception exception = catchException(() -> underTest.addRole(givenId, AccountRole.CLIENT));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountRoleAlreadyAssignedException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_ROLE_ALREADY_ASSIGNED);
    }

    @Test
    @DisplayName("Should throw CantAssignGuestRoleException when new role is Guest")
    void addRole_negative_4() {
        //given
        Long givenId = 1L;
        Set<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));

        //when
        Exception exception = catchException(() -> underTest.addRole(givenId, AccountRole.GUEST));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantAssignGuestRoleException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_ROLE_CANT_ASSIGN_GUEST);
    }

    @Test
    @DisplayName("Should throw AccountWithAdministratorRoleCantHaveMoreRolesException when account has Administrator role and there is try to add another one")
    void addRole_negative_5() {
        //given
        Long givenId = 1L;
        Set<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.ADMIN));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));

        //when
        Exception exception = catchException(() -> underTest.addRole(givenId, AccountRole.CLIENT));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountWithAdministratorRoleCantHaveMoreRolesException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_ROLE_ADMIN_MANY_ROLES);
    }

    @Test
    @DisplayName("Should throw AccountWithAdministratorRoleCantHaveMoreRolesException when account hasn't got Administrator role and there is try to add Administrator role to an account")
    void addRole_negative_6() {
        //given
        Long givenId = 1L;
        Set<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));

        //when
        Exception exception = catchException(() -> underTest.addRole(givenId, AccountRole.ADMIN));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountWithAdministratorRoleCantHaveMoreRolesException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_ROLE_ADMIN_MANY_ROLES);
    }

    @Test
    @DisplayName("Should remove role assigned to account")
    void removeRole_positive_1() {
        //given
        Long givenId = 1L;
        Set<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT, AccountRole.EMPLOYEE));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));
        given(accountRepository.save(givenAccount)).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        Account result = underTest.removeRole(givenId, AccountRole.CLIENT);

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).should().save(givenAccount);

        assertThat(result.getAccountRoles())
            .hasSize(1)
            .containsExactly(AccountRole.EMPLOYEE);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account with provided id can't be found")
    void removeRole_negative_1() {
        //given
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.empty());

        //when
        Exception exception = catchException(() -> underTest.removeRole(givenId, AccountRole.CLIENT));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotFoundException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("Should throw CantModifyArchivalAccountException when found account is archival")
    void removeRole_negative_2() {
        //given
        Long givenId = 1L;
        Set<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT, AccountRole.EMPLOYEE));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .isArchival(true)
            .accountRoles(givenRoles)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));

        //when
        Exception exception = catchException(() -> underTest.removeRole(givenId, AccountRole.CLIENT));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantModifyArchivalAccountException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_ARCHIVAL);
    }

    @Test
    @DisplayName("Should throw AccountRoleNotFoundException when account doesn't have role we want to remove")
    void removeRole_negative_3() {
        //given
        Long givenId = 1L;
        Set<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT, AccountRole.EMPLOYEE));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));

        //when
        Exception exception = catchException(() -> underTest.removeRole(givenId, AccountRole.ADMIN));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountRoleNotFoundException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_ROLE_NOT_FOUND);
    }

    @Test
    @DisplayName("Should throw CantRemoveLastRoleException when account has last role")
    void removeRole_negative_4() {
        //given
        Long givenId = 1L;
        Set<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));

        //when
        Exception exception = catchException(() -> underTest.removeRole(givenId, AccountRole.CLIENT));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantRemoveLastRoleException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_LAST_ROLE);
    }

    @Test
    @DisplayName("Should change role")
    void changeRole_positive_1() {
        //given
        Long givenId = 1L;
        Set<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));
        given(accountRepository.save(givenAccount)).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        Account result = underTest.changeRole(givenId, AccountRole.EMPLOYEE);

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).should().save(givenAccount);

        assertThat(result.getAccountRoles())
            .hasSize(1)
            .containsExactly(AccountRole.EMPLOYEE);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account with provided id can't be found")
    void changeRole_negative_1() {
        //given
        Long givenId = 1L;

        given(accountRepository.findById(givenId)).willReturn(Optional.empty());

        //when
        Exception exception = catchException(() -> underTest.changeRole(givenId, AccountRole.EMPLOYEE));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotFoundException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("Should throw CantModifyArchivalAccountException when found account is archival")
    void changeRole_negative_2() {
        //given
        Long givenId = 1L;
        Set<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .isArchival(true)
            .accountRoles(givenRoles)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));

        //when
        Exception exception = catchException(() -> underTest.changeRole(givenId, AccountRole.EMPLOYEE));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantModifyArchivalAccountException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_ARCHIVAL);
    }

    @Test
    @DisplayName("Should throw CantChangeRoleIfMoreThanOneAlreadyAssignedException when account has more than one role")
    void changeRole_negative_3() {
        //given
        Long givenId = 1L;
        Set<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT, AccountRole.EMPLOYEE));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));

        //when
        Exception exception = catchException(() -> underTest.changeRole(givenId, AccountRole.EMPLOYEE));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantChangeRoleIfMoreThanOneAlreadyAssignedException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_ROLE_MORE_THAN_ONE);
    }

    @Test
    @DisplayName("Should throw AccountRoleAlreadyAssignedException when account has new role already assigned")
    void changeRole_negative_4() {
        //given
        Long givenId = 1L;
        Set<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.EMPLOYEE));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));

        //when
        Exception exception = catchException(() -> underTest.changeRole(givenId, AccountRole.EMPLOYEE));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountRoleAlreadyAssignedException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_ROLE_ALREADY_ASSIGNED);
    }

    @Test
    @DisplayName("Should throw CantAssignGuestRoleException there is try to assign guest role")
    void changeRole_negative_5() {
        //given
        Long givenId = 1L;
        Set<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(givenAccount));

        //when
        Exception exception = catchException(() -> underTest.changeRole(givenId, AccountRole.GUEST));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantAssignGuestRoleException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_ROLE_CANT_ASSIGN_GUEST);
    }
}