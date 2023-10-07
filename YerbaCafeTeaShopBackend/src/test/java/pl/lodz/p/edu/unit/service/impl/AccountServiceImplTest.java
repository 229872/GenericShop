package pl.lodz.p.edu.unit.service.impl;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Disabled;
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
import pl.lodz.p.edu.dataaccess.model.sub.AccountState;
import pl.lodz.p.edu.dataaccess.repository.api.AccountRepository;
import pl.lodz.p.edu.exception.*;
import pl.lodz.p.edu.logic.model.NewPersonalInformation;
import pl.lodz.p.edu.logic.service.impl.AccountServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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
        Account account = Account.builder().login("login").build();
        given(accountRepository.save(account)).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        Account result = underTest.create(account);

        //then
        then(accountRepository).should().save(account);
        assertThat(result)
            .isEqualTo(account);
    }

    @Test
    @DisplayName("Should throw AccountLoginConflictException when new Account has same login")
    void create_should_throw_account_login_conflict_exception() {
        //given
        Account account = Account.builder().login("login").build();
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
        Account account = Account.builder().email("email@example.com").build();
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
        Account account = Account.builder().person(person).build();
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
        Account account = Account.builder().person(person).build();
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
    @DisplayName("Should block active account")
    void block_should_block_active_account() {
        //given
        Account account = Account.builder().accountState(AccountState.ACTIVE).build();
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
    @DisplayName("Should throw AccountNotActiveException when account is blocked")
    void block_should_throw_AccountNotActiveException_when_account_is_blocked() {
        //given
        Account account = Account.builder().accountState(AccountState.BLOCKED).build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

        //when
        Exception exception = catchException(() -> underTest.block(givenId));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotActiveException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_ACTIVE);
    }

    @Test
    @DisplayName("Should throw AccountNotActiveException when account is archival")
    void block_should_throw_AccountNotActiveException_when_account_is_archival() {
        //given
        Account account = Account.builder().accountState(AccountState.ARCHIVAL).build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

        //when
        Exception exception = catchException(() -> underTest.block(givenId));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotActiveException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_ACTIVE);
    }

    @Test
    @DisplayName("Should throw AccountNotActiveException when account is not verified")
    void block_should_throw_AccountNotActiveException_when_account_is_not_verified() {
        //given
        Account account = Account.builder().accountState(AccountState.NOT_VERIFIED).build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

        //when
        Exception exception = catchException(() -> underTest.block(givenId));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotActiveException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_ACTIVE);
    }

    @Test
    @DisplayName("Should unblock blocked account")
    void unblock_should_unblock_blocked_account() {
        //given
        Account account = Account.builder().accountState(AccountState.BLOCKED).build();
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
    @DisplayName("Should throw AccountNotBlockedException when account is active")
    void unblock_should_throw_AccountNotBlockedException_when_account_is_active() {
        //given
        Account account = Account.builder().accountState(AccountState.ACTIVE).build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

        //when
        Exception exception = catchException(() -> underTest.unblock(givenId));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotBlockedException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_BLOCKED);
    }

    @Test
    @DisplayName("Should throw AccountNotBlockedException when account is archival")
    void unblock_should_throw_AccountNotBlockedException_when_account_is_archival() {
        //given
        Account account = Account.builder().accountState(AccountState.ARCHIVAL).build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

        //when
        Exception exception = catchException(() -> underTest.unblock(givenId));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotBlockedException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_BLOCKED);
    }

    @Test
    @DisplayName("Should throw AccountNotBlockedException when account is not verified")
    void unblock_should_throw_AccountNotBlockedException_when_account_is_not_verified() {
        //given
        Account account = Account.builder().accountState(AccountState.NOT_VERIFIED).build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

        //when
        Exception exception = catchException(() -> underTest.unblock(givenId));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotBlockedException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_BLOCKED);
    }

    @Test
    @DisplayName("Should archive active account")
    void archive_should_archive_active_account() {
        //given
        Account account = Account.builder().accountState(AccountState.ACTIVE).build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));
        given(accountRepository.save(account)).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        Account result = underTest.archive(givenId);

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).should().save(account);

        assertEquals(AccountState.ARCHIVAL, result.getAccountState());
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
    @DisplayName("Should throw AccountAlreadyArchivalException when account is already archival")
    void archive_should_throw_AccountAlreadyArchivalException() {
        //given
        Account account = Account.builder().accountState(AccountState.ARCHIVAL).build();
        Long givenId = 1L;
        given(accountRepository.findById(givenId)).willReturn(Optional.of(account));

        //when
        Exception exception = catchException(() -> underTest.archive(givenId));

        //then
        then(accountRepository).should().findById(givenId);
        then(accountRepository).shouldHaveNoMoreInteractions();

        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountAlreadyArchivalException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_ALREADY_ARCHIVAL);
    }

    @Test
    @Disabled
    void addRole() {
    }

    @Test
    @Disabled
    void removeRole() {
    }

    @Test
    @Disabled
    void changeRole() {
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