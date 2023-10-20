package pl.lodz.p.edu.unit.service;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.edu.dataaccess.model.Account;
import pl.lodz.p.edu.dataaccess.model.Address;
import pl.lodz.p.edu.dataaccess.model.Person;
import pl.lodz.p.edu.dataaccess.model.sub.AccountRole;
import pl.lodz.p.edu.dataaccess.model.sub.AccountState;
import pl.lodz.p.edu.dataaccess.repository.api.AccountRepository;
import pl.lodz.p.edu.exception.*;
import pl.lodz.p.edu.exception.account.*;
import pl.lodz.p.edu.logic.model.NewPersonalInformation;
import pl.lodz.p.edu.logic.service.impl.AccountServiceImpl;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("Unit tests for AccountService")
@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl underTest;

    @Test
    @DisplayName("Should return empty list")
    void findAll_should_return_empty_list() {
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
    void findAll_should_return_list_with_elements() {
        //given
        Account[] accounts = {
            Account.builder().login("login1").build(),
            Account.builder().login("login2").build()
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
    void findById_should_return_account() {
        //given
        Long givenId = 1L;
        Account account = Account.builder().id(givenId).login("login").build();
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

        //when
        Account result = underTest.findById(givenId);

        //then
        then(accountRepository).should().findById(givenId);
        assertThat(result)
            .isEqualTo(account);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException if account with id is not found")
    void findById_should_throw_AccountNotFoundException() {
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
    @DisplayName("Should return account if account with login is found")
    void findByLogin_should_return_account() {
        //given
        String givenLogin = "login";
        Account account = Account.builder().login(givenLogin).build();
        given(accountRepository.findByLogin(givenLogin)).willReturn(Optional.of(account));

        //when
        Account result = underTest.findByLogin(givenLogin);

        //then
        then(accountRepository).should().findByLogin(givenLogin);
        assertThat(result)
            .isEqualTo(account);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException if account with login is not found")
    void findByLogin_should_throw_AccountNotFoundException() {
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
    @DisplayName("Should create account")
    void create_should_create_account() {
        //given
        Account account = Account.builder()
            .login("login")
            .accountState(AccountState.ACTIVE)
            .accountRoles(new HashSet<>(Set.of(AccountRole.CLIENT)))
            .build();
        given(accountRepository.save(account)).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        Account result = underTest.create(account);

        //then
        then(accountRepository).should().save(account);
        assertThat(result)
            .isEqualTo(account);
    }

    @Test
    @DisplayName("Should throw CantCreateAccountWithManyRolesException when account has more than one role")
    void create_should_throw_CantCreateAccountWithManyRolesException() {
        //given
        Account account = Account.builder()
            .login("login")
            .accountState(AccountState.ACTIVE)
            .accountRoles(new HashSet<>(Set.of(AccountRole.CLIENT, AccountRole.EMPLOYEE)))
            .build();

        //when
        Exception exception = catchException(() -> underTest.create(account));

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
    void create_should_throw_CantAssignGuestRoleException() {
        //given
        Account account = Account.builder()
            .login("login")
            .accountState(AccountState.ACTIVE)
            .accountRoles(new HashSet<>(Set.of(AccountRole.GUEST)))
            .build();

        //when
        Exception exception = catchException(() -> underTest.create(account));

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
    void create_should_throw_CantCreateAccountWithNotVerifiedStatusException() {
        //given
        Account account = Account.builder()
            .login("login")
            .accountState(AccountState.NOT_VERIFIED)
            .accountRoles(new HashSet<>(Set.of(AccountRole.CLIENT)))
            .build();

        //when
        Exception exception = catchException(() -> underTest.create(account));

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
    void create_should_throw_account_login_conflict_exception() {
        //given
        Account account = Account.builder()
            .login("login")
            .accountState(AccountState.ACTIVE)
            .accountRoles(new HashSet<>(Set.of(AccountRole.CLIENT)))
            .build();
        var cause = new ConstraintViolationException("Database violation occurred", null, "accounts_login_key");
        var dataIntegrityViolationException = new DataIntegrityViolationException("Violation occurred", cause);
        given(accountRepository.save(account)).willThrow(dataIntegrityViolationException);

        //when
        Exception exception = catchException(() -> underTest.create(account));

        //then
        then(accountRepository).should().save(account);
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountLoginConflictException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_CONFLICT_LOGIN);
    }

    @Test
    @DisplayName("Should throw AccountEmailConflictException when new Account has same email")
    void create_should_throw_account_email_conflict_exception() {
        //given
        Account account = Account.builder()
            .email("email@example.com")
            .accountState(AccountState.ACTIVE)
            .accountRoles(new HashSet<>(Set.of(AccountRole.CLIENT)))
            .build();
        var cause = new ConstraintViolationException("Database violation occurred", null, "accounts_email_key");
        var dataIntegrityViolationException = new DataIntegrityViolationException("Violation occurred", cause);
        given(accountRepository.save(account)).willThrow(dataIntegrityViolationException);

        //when
        Exception exception = catchException(() -> underTest.create(account));

        //then
        then(accountRepository).should().save(account);
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountEmailConflictException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_CONFLICT_EMAIL);
    }

    @Test
    @DisplayName("Should update found account without one value")
    void update_should_modify_one_value() {
        //given
        Address address = buildFullAddress("postalCode", "country", "city", "street", 1);
        Person person = buildFullPerson("firstName", "lastName", address);
        Account account = Account.builder().person(person).isArchival(false).build();
        Long givenId = 1L;

        String newFirstName = "newFirstName";
        NewPersonalInformation newPersonalInfo = NewPersonalInformation.builder().firstName(newFirstName).build();

        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));
        given(accountRepository.save(account)).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        Account result = underTest.updatePersonalInformation(givenId, newPersonalInfo);

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).should().save(account);

        assertThat(result.getPerson().getFirstName())
            .isEqualTo(newFirstName);

        assertThat(result.getPerson().getLastName())
            .isEqualTo(person.getLastName());
    }

    @Test
    @DisplayName("Should update found account without all values")
    void update_should_modify_all_values() {
        //given
        Address address = buildFullAddress("postalCode", "country", "city", "street", 1);
        Person person = buildFullPerson("firstName", "lastName", address);
        Account account = Account.builder().person(person).isArchival(false).build();
        Long givenId = 1L;

        String newFirstName = "newFirstName";
        String newLastName = "newLastName";
        String newPostalCode = "newPostalCode";
        String newCountry = "newCountry";
        String newCity = "newCity";
        String newStreet = "newStreet";
        Integer newHouseNumber = 2;
        NewPersonalInformation newPersonalInfo = new NewPersonalInformation(newFirstName, newLastName, newPostalCode,
            newCountry, newCity, newStreet, newHouseNumber);

        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));
        given(accountRepository.save(account)).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        Account result = underTest.updatePersonalInformation(givenId, newPersonalInfo);

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).should().save(account);

        Person resultPerson = result.getPerson();
        assertEquals(newFirstName, resultPerson.getFirstName());
        assertEquals(newLastName, resultPerson.getLastName());
        assertEquals(newPostalCode, resultPerson.getPostalCode());
        assertEquals(newCountry, resultPerson.getCountry());
        assertEquals(newCity, resultPerson.getCity());
        assertEquals(newStreet, resultPerson.getStreet());
        assertEquals(newHouseNumber, resultPerson.getHouseNumber());
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account can't be found during update")
    void update_should_throw_AccountNotFoundException() {
        //given
        Long givenId = 1L;
        NewPersonalInformation newInfo = NewPersonalInformation.builder().firstName("newFirstName").build();
        given(accountRepository.findById(givenId)).willReturn(Optional.empty());

        //when
        Exception exception = catchException(() -> underTest.updatePersonalInformation(givenId, newInfo));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotFoundException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("Should throw CantModifyArchivalAccountException when account is archival")
    void update_should_throw_CantModifyArchivalAccountException() {
        //given
        Long givenId = 1L;
        Address address = buildFullAddress("postalCode", "country", "city", "street", 1);
        Person person = buildFullPerson("firstName", "lastName", address);
        Account account = Account.builder().person(person).isArchival(true).build();
        NewPersonalInformation newInfo = NewPersonalInformation.builder().firstName("newFirstName").build();
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

        //when
        Exception exception = catchException(() -> underTest.updatePersonalInformation(givenId, newInfo));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantModifyArchivalAccountException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_ARCHIVAL);
    }

    @Test
    @DisplayName("Should block active account")
    void block_should_block_active_account() {
        //given
        Account account = Account.builder().isArchival(false).accountState(AccountState.ACTIVE).build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));
        given(accountRepository.save(account)).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        Account result = underTest.block(givenId);

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).should().save(account);

        assertEquals(AccountState.BLOCKED, result.getAccountState());
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account is not found")
    void block_should_throw_AccountNotfoundException() {
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
    void block_should_throw_CantModifyArchivalAccountException_when_account_is_archival() {
        //given
        Account account = Account.builder().isArchival(true).accountState(AccountState.ACTIVE).build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

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
    void block_should_throw_AOperationNotAllowedWithActualAccountStateException_when_account_is_not_verified() {
        //given
        Account account = Account.builder().isArchival(false).accountState(AccountState.NOT_VERIFIED).build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

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
    void block_should_throw_OperationNotAllowedWithActualAccountStateException_when_account_is_blocked() {
        //given
        Account account = Account.builder().isArchival(false).accountState(AccountState.BLOCKED).build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

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
    void unblock_should_unblock_blocked_account() {
        //given
        Account account = Account.builder().isArchival(false).accountState(AccountState.BLOCKED).build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

        //when
        Account result = underTest.unblock(givenId);

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).should().save(account);

        assertEquals(AccountState.ACTIVE, result.getAccountState());
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account can't be found")
    void unblock_should_throw_AccountNotFoundException() {
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
    void unblock_should_throw_CantModifyArchivalAccountException_when_account_is_archival() {
        //given
        Account account = Account.builder().isArchival(true).build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

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
    void unblock_should_throw_OperationNotAllowedWithActualAccountStateException_when_account_is_not_verified() {
        //given
        Account account = Account.builder().isArchival(false).accountState(AccountState.NOT_VERIFIED).build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

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
    void unblock_should_throw_OperationNotAllowedWithActualAccountStateException_when_account_is_active() {
        //given
        Account account = Account.builder().isArchival(false).accountState(AccountState.ACTIVE).build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

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
    void archive_should_archive_active_account() {
        //given
        Person person = Person.builder().address(Address.builder().build()).build();
        Account account = Account.builder().isArchival(false).accountState(AccountState.ACTIVE).person(person).build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));
        given(accountRepository.save(account)).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        Account result = underTest.archive(givenId);

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).should().save(account);

        assertEquals(true, result.isArchival());
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account can't be found during archive")
    void archive_should_throw_AccountNotFoundException() {
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
    void archive_should_throw_CantModifyArchivalAccountException() {
        //given
        Account account = Account.builder().isArchival(true).build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

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
    void addRole_should_add_new_role() {
        //given
        Account account = Account.builder().isArchival(false).accountRoles(new HashSet<>(Set.of(AccountRole.CLIENT))).build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));
        given(accountRepository.save(account)).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        Account result = underTest.addRole(givenId, AccountRole.EMPLOYEE);

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).should().save(account);

        assertThat(result.getAccountRoles())
            .hasSize(2)
            .contains(AccountRole.CLIENT, AccountRole.EMPLOYEE);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account can't be found")
    void addRole_should_throw_AccountNotFoundException() {
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
    void addRole_should_throw_CantModifyArchivalAccountException() {
        //given
        Long givenId = 1L;
        Account account = Account.builder().isArchival(true).accountRoles(new HashSet<>(Set.of(AccountRole.CLIENT))).build();
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

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
    @DisplayName("Should throw AccountRoleAlreadyAssignedException when account already has new role")
    void addRole_should_throw_AccountRoleAlreadyAssignedException() {
        //given
        Long givenId = 1L;
        Account account = Account.builder().isArchival(false).accountRoles(new HashSet<>(Set.of(AccountRole.CLIENT))).build();
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

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
    void addRole_should_throw_CantAssignGuestRoleException() {
        //given
        Long givenId = 1L;
        Account account = Account.builder().isArchival(false).accountRoles(new HashSet<>(Set.of(AccountRole.CLIENT))).build();
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

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
    void addRole_should_throw_AccountWithAdministratorRoleCantHaveMoreRolesException_when_Admin_is_already_assigned() {
        //given
        Long givenId = 1L;
        Account account = Account.builder().isArchival(false).accountRoles(new HashSet<>(Set.of(AccountRole.ADMIN))).build();
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

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
    void addRole_should_throw_AccountWithAdministratorRoleCantHaveMoreRolesException_when_there_is_try_to_add_Admin_role() {
        //given
        Long givenId = 1L;
        Account account = Account.builder().isArchival(false).accountRoles(new HashSet<>(Set.of(AccountRole.CLIENT))).build();
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

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
    void removeRole_should_remove_role() {
        //given
        Account account = Account.builder()
            .isArchival(false)
            .accountRoles(new HashSet<>(Set.of(AccountRole.CLIENT, AccountRole.EMPLOYEE)))
            .build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));
        given(accountRepository.save(account)).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        Account result = underTest.removeRole(givenId, AccountRole.CLIENT);

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).should().save(account);

        assertThat(result.getAccountRoles())
            .hasSize(1)
            .containsExactly(AccountRole.EMPLOYEE);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account with provided id can't be found")
    void removeRole_should_throw_AccountNotFoundException() {
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
    void removeRole_should_throw_CantModifyArchivalAccountException() {
        //given
        Account account = Account.builder()
            .isArchival(true)
            .accountRoles(new HashSet<>(Set.of(AccountRole.CLIENT, AccountRole.EMPLOYEE)))
            .build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

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
    void removeRole_should_throw_AccountRoleNotFoundException() {
        //given
        Account account = Account.builder()
            .isArchival(false)
            .accountRoles(new HashSet<>(Set.of(AccountRole.CLIENT, AccountRole.EMPLOYEE)))
            .build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

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
    void removeRole_should_throw_CantRemoveLastRoleException() {
        //given
        Account account = Account.builder()
            .isArchival(false)
            .accountRoles(new HashSet<>(Set.of(AccountRole.CLIENT)))
            .build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

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
    void changeRole_should_change_role() {
        //given
        Account account = Account.builder().isArchival(false).accountRoles(new HashSet<>(Set.of(AccountRole.CLIENT))).build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));
        given(accountRepository.save(account)).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        Account result = underTest.changeRole(givenId, AccountRole.EMPLOYEE);

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).should().save(account);

        assertThat(result.getAccountRoles())
            .hasSize(1)
            .containsExactly(AccountRole.EMPLOYEE);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account with provided id can't be found")
    void changeRole_should_throw_AccountNotFoundException() {
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
    void changeRole_should_throw_CantModifyArchivalAccountException() {
        //given
        Long givenId = 1L;
        Account account = Account.builder().isArchival(true).accountRoles(new HashSet<>(Set.of(AccountRole.CLIENT))).build();
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

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
    void changeRole_should_throw_CantChangeRoleIfMoreThanOneAlreadyAssignedException() {
        //given
        Long givenId = 1L;
        Account account = Account.builder()
            .isArchival(false)
            .accountRoles(new HashSet<>(Set.of(AccountRole.CLIENT, AccountRole.EMPLOYEE)))
            .build();
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

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
    void changeRole_should_throw_AccountRoleAlreadyAssignedException() {
        //given
        Long givenId = 1L;
        Account account = Account.builder()
            .isArchival(false)
            .accountRoles(new HashSet<>(Set.of(AccountRole.EMPLOYEE)))
            .build();
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

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
    @DisplayName("Should throw CantAssignGuestRoleException when account has new role already assigned")
    void changeRole_should_throw_CantAssignGuestRoleException() {
        //given
        Long givenId = 1L;
        Account account = Account.builder()
            .isArchival(false)
            .accountRoles(new HashSet<>(Set.of(AccountRole.CLIENT)))
            .build();
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

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

    private Address buildFullAddress(String postalCode, String country, String city, String street,
                                     Integer houseNumber) {
        return Address.builder()
            .postalCode(postalCode)
            .country(country)
            .city(city)
            .street(street)
            .houseNumber(houseNumber)
            .build();
    }

    private Person buildFullPerson(String firstName, String lastName, Address address) {
        return Person.builder()
            .firstName(firstName)
            .lastName(lastName)
            .address(address)
            .build();
    }
}