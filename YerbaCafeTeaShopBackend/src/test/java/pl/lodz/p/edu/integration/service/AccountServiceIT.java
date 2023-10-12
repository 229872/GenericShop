package pl.lodz.p.edu.integration.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.edu.dataaccess.model.Account;
import pl.lodz.p.edu.dataaccess.model.Address;
import pl.lodz.p.edu.dataaccess.model.Person;
import pl.lodz.p.edu.dataaccess.model.sub.AccountRole;
import pl.lodz.p.edu.dataaccess.model.sub.AccountState;
import pl.lodz.p.edu.exception.*;
import pl.lodz.p.edu.logic.model.NewPersonalInformation;
import pl.lodz.p.edu.logic.service.api.AccountService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pl.lodz.p.edu.integration.service.AccountServiceIT.TestData.buildDefaultAccount;
import static pl.lodz.p.edu.integration.service.AccountServiceIT.TestData.counter;

@SpringBootTest
@ActiveProfiles("it")
class AccountServiceIT {

    @Autowired
    private AccountService underTest;

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

        counter = 1;
    }

    @Test
    @DisplayName("Should return empty list")
    void findAll_should_return_empty_list() {
        //given

        //when
        List<Account> result = underTest.findAll();

        //then
        assertThat(result)
            .isEmpty();
    }

    @Test
    @DisplayName("Should return list with elements")
    void findAll_should_return_list_with_elements() {
        //given
        Account[] accounts = {
            buildDefaultAccount(),
            buildDefaultAccount()
        };
        txTemplate.execute(status -> {
            Arrays.stream(accounts).forEach(account -> em.persist(account));
            return status;
        });

        //when
        List<Account> result = underTest.findAll();

        //then
        assertThat(result)
            .hasSize(2)
            .containsExactly(accounts);
    }


    @Test
    @DisplayName("Should return account if account with id is found")
    void findById_should_return_account() {
        //given
        Account account = buildDefaultAccount();
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });

        Long givenId = account.getId();

        //when
        Account result = underTest.findById(givenId);

        //then
        assertThat(result)
            .isEqualTo(account);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException if account with id is not found")
    void findById_should_throw_AccountNotFoundException() {
        //given
        Long givenId = 1L;

        //when
        Exception exception = catchException(() -> underTest.findById(1L));

        //then
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
        Account account = buildDefaultAccount();
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        String givenLogin = account.getLogin();

        //when
        Account result = underTest.findByLogin(givenLogin);

        //then
        assertThat(result)
            .isEqualTo(account);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException if account with login is not found")
    void findByLogin_should_throw_AccountNotFoundException() {
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
    @DisplayName("Should create account")
    void create_should_create_account() {
        //given
        Account account = buildDefaultAccount();

        //when
        Account result = underTest.create(account);

        //then
        assertThat(result)
            .isEqualTo(account);

        txTemplate.execute(status -> {
            List<Account> databaseAccounts = em.createQuery("from Account ", Account.class).getResultList();

            assertThat(databaseAccounts)
                .hasSize(1)
                .containsExactly(account);
            return status;
        });
    }

    @Test
    @DisplayName("Should throw AccountLoginConflictException when new Account has same login")
    void create_should_throw_account_login_conflict_exception() {
        //given
        Account account = buildDefaultAccount();
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });

        Account accountWithConflictLogin = buildDefaultAccount();
        accountWithConflictLogin.setLogin(account.getLogin());

        //when
        Exception exception = catchException(() -> underTest.create(accountWithConflictLogin));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountLoginConflictException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_CONFLICT_LOGIN);
    }

    @Test
    @DisplayName("Should throw AccountEmailConflictException when new Account has same email")
    void create_should_throw_account_email_conflict_exception() {
        //given
        Account account = buildDefaultAccount();
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });

        Account accountWithConflictEmail = buildDefaultAccount();
        accountWithConflictEmail.setEmail(account.getEmail());

        //when
        Exception exception = catchException(() -> underTest.create(accountWithConflictEmail));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountEmailConflictException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_CONFLICT_EMAIL);
    }

    @Test
    @Disabled
    @DisplayName("Should update found account without one value")
    void update_should_modify_one_value() {
        //given
        Account account = buildDefaultAccount();

        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        Long givenId = account.getId();

        String newFirstName = "newFirstName";
        NewPersonalInformation newPersonalInfo = NewPersonalInformation.builder().firstName(newFirstName).build();

        //when
        Account result = underTest.updatePersonalInformation(givenId, newPersonalInfo);

        //then
        assertThat(result.getPerson().getFirstName())
            .isEqualTo(newFirstName);

        assertThat(result.getPerson().getLastName())
            .isEqualTo(account.getPerson().getLastName());
    }

    @Test
    @Disabled
    @DisplayName("Should update found account without all values")
    void update_should_modify_all_values() {
        //given
        Account account = buildDefaultAccount();
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        Long givenId = account.getId();

        String newFirstName = "newFirstName";
        String newLastName = "newLastName";
        String newPostalCode = "newPostalCode";
        String newCountry = "newCountry";
        String newCity = "newCity";
        String newStreet = "newStreet";
        Integer newHouseNumber = 2;
        NewPersonalInformation newPersonalInfo = new NewPersonalInformation(newFirstName, newLastName, newPostalCode,
            newCountry, newCity, newStreet, newHouseNumber);

        //when
        Account result = underTest.updatePersonalInformation(givenId, newPersonalInfo);

        //then
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
    @Disabled
    @DisplayName("Should throw AccountNotFoundException when account can't be found during update")
    void update_should_throw_AccountNotFoundException() {
        //given
        Long givenId = 1L;
        NewPersonalInformation newInfo = NewPersonalInformation.builder().firstName("newFirstName").build();

        //when
        Exception exception = catchException(() -> underTest.updatePersonalInformation(givenId, newInfo));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotFoundException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_FOUND);
    }

    @Test
    @Disabled
    @DisplayName("Should block active account")
    void block_should_block_active_account() {
        //given
        Account account = Account.builder().accountState(AccountState.ACTIVE).build();
        Long givenId = 1L;

        //when
        Account result = underTest.block(givenId);

        //then
        assertEquals(AccountState.BLOCKED, result.getAccountState());
    }

    @Test
    @Disabled
    @DisplayName("Should throw AccountNotFoundException when account is not found")
    void block_should_throw_AccountNotfoundException() {
        //given
        Long givenId = 1L;

        //when
        Exception exception = catchException(() -> underTest.block(givenId));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotFoundException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_FOUND);
    }

    @Test
    @Disabled
    @DisplayName("Should throw AccountNotActiveException when account is blocked")
    void block_should_throw_AccountNotActiveException_when_account_is_blocked() {
        //given
        Account account = Account.builder().accountState(AccountState.BLOCKED).build();
        Long givenId = 1L;

        //when
        Exception exception = catchException(() -> underTest.block(givenId));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotActiveException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_ACTIVE);
    }

    @Test
    @Disabled
    @DisplayName("Should throw AccountNotActiveException when account is archival")
    void block_should_throw_AccountNotActiveException_when_account_is_archival() {
        //given
        Account account = Account.builder().accountState(AccountState.ARCHIVAL).build();
        Long givenId = 1L;

        //when
        Exception exception = catchException(() -> underTest.block(givenId));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotActiveException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_ACTIVE);
    }

    @Test
    @Disabled
    @DisplayName("Should throw AccountNotActiveException when account is not verified")
    void block_should_throw_AccountNotActiveException_when_account_is_not_verified() {
        //given
        Account account = Account.builder().accountState(AccountState.NOT_VERIFIED).build();
        Long givenId = 1L;

        //when
        Exception exception = catchException(() -> underTest.block(givenId));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotActiveException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_ACTIVE);
    }

    @Test
    @Disabled
    @DisplayName("Should unblock blocked account")
    void unblock_should_unblock_blocked_account() {
        //given
        Account account = Account.builder().accountState(AccountState.BLOCKED).build();
        Long givenId = 1L;

        //when
        Account result = underTest.unblock(givenId);

        //then
        assertEquals(AccountState.ACTIVE, result.getAccountState());
    }

    @Test
    @Disabled
    @DisplayName("Should throw AccountNotFoundException when account can't be found")
    void unblock_should_throw_AccountNotFoundException() {
        //given
        Long givenId = 1L;

        //when
        Exception exception = catchException(() -> underTest.unblock(givenId));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotFoundException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_FOUND);
    }

    @Test
    @Disabled
    @DisplayName("Should throw AccountNotBlockedException when account is active")
    void unblock_should_throw_AccountNotBlockedException_when_account_is_active() {
        //given
        Account account = Account.builder().accountState(AccountState.ACTIVE).build();
        Long givenId = 1L;

        //when
        Exception exception = catchException(() -> underTest.unblock(givenId));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotBlockedException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_BLOCKED);
    }

    @Test
    @Disabled
    @DisplayName("Should throw AccountNotBlockedException when account is archival")
    void unblock_should_throw_AccountNotBlockedException_when_account_is_archival() {
        //given
        Account account = Account.builder().accountState(AccountState.ARCHIVAL).build();
        Long givenId = 1L;

        //when
        Exception exception = catchException(() -> underTest.unblock(givenId));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotBlockedException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_BLOCKED);
    }

    @Test
    @Disabled
    @DisplayName("Should throw AccountNotBlockedException when account is not verified")
    void unblock_should_throw_AccountNotBlockedException_when_account_is_not_verified() {
        //given
        Account account = Account.builder().accountState(AccountState.NOT_VERIFIED).build();
        Long givenId = 1L;

        //when
        Exception exception = catchException(() -> underTest.unblock(givenId));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotBlockedException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_BLOCKED);
    }

    @Test
    @Disabled
    @DisplayName("Should archive active account")
    void archive_should_archive_active_account() {
        //given
        Account account = Account.builder().accountState(AccountState.ACTIVE).build();
        Long givenId = 1L;

        //when
        Account result = underTest.archive(givenId);

        //then
        assertEquals(AccountState.ARCHIVAL, result.getAccountState());
    }

    @Test
    @Disabled
    @DisplayName("Should throw AccountNotFoundException when account can't be found during archive")
    void archive_should_throw_AccountNotFoundException() {
        //given
        Long givenId = 1L;

        //when
        Exception exception = catchException(() -> underTest.archive(givenId));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountNotFoundException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_FOUND);
    }

    @Test
    @Disabled
    @DisplayName("Should throw AccountAlreadyArchivalException when account is already archival")
    void archive_should_throw_AccountAlreadyArchivalException() {
        //given
        Account account = Account.builder().accountState(AccountState.ARCHIVAL).build();
        Long givenId = 1L;

        //when
        Exception exception = catchException(() -> underTest.archive(givenId));

        //then
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




    static class TestData {
        static int counter = 1;

        static final String defaultPostalCode = "postalCode";
        static final String defaultCountry = "country";
        static final String defaultCity = "city";
        static final String defaultStreet = "street";
        static final Integer defaultHouseNumber = 30;

        static final String defaultLogin = "login";
        static final String defaultEmail = "email";
        static final String defaultPassword = "password";
        static final String defaultLocale = "locale";
        static final AccountState defaultAccountState = AccountState.ACTIVE;
        static final Set<AccountRole> defaultAccountRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        static final String defaultCreatedBy = "testUser";

        static final String defaultFirstName = "firstName";
        static final String defaultLastName = "lastName";

        static Address buildFullAddress(String postalCode, String country, String city, String street,
                                         Integer houseNumber, String createdBy) {
            return Address.builder()
                .postalCode(postalCode)
                .country(country)
                .city(city)
                .street(street)
                .houseNumber(houseNumber)
                .createdBy(createdBy)
                .build();
        }

        static Person buildFullPerson(String firstName, String lastName, Address address, String createdBy) {
            return Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address(address)
                .createdBy(createdBy)
                .build();
        }

        static Account buildFullAccount(String login, String email, String password, String locale, Person person,
                                         AccountState accountState, Set<AccountRole> accountRoles, String createdBy) {
            return Account.builder()
                .login(login)
                .email(email)
                .password(password)
                .locale(locale)
                .person(person)
                .accountState(accountState)
                .accountRoles(accountRoles)
                .createdBy(createdBy)
                .build();
        }


        static Address buildDefaultAddress() {
            return buildFullAddress(defaultPostalCode, defaultCountry, defaultCity, defaultStreet, defaultHouseNumber,
                defaultCreatedBy);
        }

        static Person buildDefaultPerson() {
            return buildFullPerson(defaultFirstName, defaultLastName, buildDefaultAddress(), defaultCreatedBy);
        }

        static Account buildDefaultAccount() {
            String uniqueLogin = defaultLogin + counter;
            String uniqueEmail = defaultEmail + counter;
            counter++;
            return buildFullAccount(uniqueLogin, uniqueEmail, defaultPassword, defaultLocale, buildDefaultPerson(),
                defaultAccountState, defaultAccountRoles, defaultCreatedBy);
        }
    }
}