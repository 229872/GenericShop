package pl.lodz.p.edu.integration.service;

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
    @DisplayName("Should block active account")
    void block_should_block_active_account() {
        //given
        Account account = buildDefaultAccount();
        account.setAccountState(AccountState.ACTIVE);
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        Long givenId = account.getId();

        //when
        Account result = underTest.block(givenId);

        //then
        assertEquals(AccountState.BLOCKED, result.getAccountState());
    }

    @Test
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
    @DisplayName("Should throw CantModifyArchivalAccountException when account is archival")
    void block_should_throw_CantModifyArchivalAccountException_when_account_is_archival() {
        //given
        Account account = buildDefaultAccount();

        txTemplate.execute(status -> {
            em.persist(account);
            account.setArchival(true);
            return status;
        });

        Long givenId = account.getId();

        //when
        Exception exception = catchException(() -> underTest.block(givenId));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantModifyArchivalAccountException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_ARCHIVAL);
    }

    @Test
    @DisplayName("Should throw OperationNotAllowedWithActualAccountStateException when account is not verified")
    void block_should_throw_OperationNotAllowedWithActualAccountStateException_when_account_is_not_verified() {
        //given
        Account account = buildDefaultAccount();
        account.setAccountState(AccountState.NOT_VERIFIED);

        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        Long givenId = account.getId();

        //when
        Exception exception = catchException(() -> underTest.block(givenId));

        //then
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
        Account account = buildDefaultAccount();
        account.setAccountState(AccountState.BLOCKED);

        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        Long givenId = account.getId();

        //when
        Exception exception = catchException(() -> underTest.block(givenId));

        //then
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
        Account account = buildDefaultAccount();
        account.setAccountState(AccountState.BLOCKED);

        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        Long givenId = account.getId();

        //when
        Account result = underTest.unblock(givenId);

        //then
        assertEquals(AccountState.ACTIVE, result.getAccountState());
    }

    @Test
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
    @DisplayName("Should throw CantModifyArchivalAccountException when account is archival")
    void unblock_should_throw_CantModifyArchivalAccountException_when_account_is_archival() {
        //given
        Account account = buildDefaultAccount();

        txTemplate.execute(status -> {
            em.persist(account);
            account.setArchival(true);
            return status;
        });
        Long givenId = account.getId();

        //when
        Exception exception = catchException(() -> underTest.unblock(givenId));

        //then
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
        Account account = buildDefaultAccount();
        account.setAccountState(AccountState.NOT_VERIFIED);

        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        Long givenId = account.getId();

        //when
        Exception exception = catchException(() -> underTest.unblock(givenId));

        //then
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
        Account account = buildDefaultAccount();
        account.setAccountState(AccountState.ACTIVE);

        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        Long givenId = account.getId();

        //when
        Exception exception = catchException(() -> underTest.unblock(givenId));

        //then
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
        Account account = buildDefaultAccount();
        account.setAccountState(AccountState.ACTIVE);

        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        Long givenId = account.getId();

        //when
        Account result = underTest.archive(givenId);

        //then
        assertEquals(true, result.isArchival());
    }

    @Test
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
    @DisplayName("Should throw CantModifyArchivalAccountException when account is already archival")
    void archive_should_throw_CantModifyArchivalAccountException() {
        //given
        Account account = buildDefaultAccount();

        txTemplate.execute(status -> {
            em.persist(account);
            account.setArchival(true);
            return status;
        });
        Long givenId = account.getId();

        //when
        Exception exception = catchException(() -> underTest.archive(givenId));

        //then
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
        Account account = buildDefaultAccount();
        account.setAccountRoles(new HashSet<>(Set.of(AccountRole.CLIENT)));
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        Long givenId = account.getId();

        //when
        Account result = underTest.addRole(givenId, AccountRole.EMPLOYEE);

        //then
        assertThat(result.getAccountRoles())
            .hasSize(2)
            .contains(AccountRole.CLIENT, AccountRole.EMPLOYEE);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account can't be found")
    void addRole_should_throw_AccountNotFoundException() {
        //given
        Long givenId = 1L;

        //when
        Exception exception = catchException(() -> underTest.addRole(givenId, AccountRole.EMPLOYEE));

        //then
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
        Account account = buildDefaultAccount();
        account.setAccountRoles(new HashSet<>(Set.of(AccountRole.CLIENT)));
        txTemplate.execute(status -> {
            em.persist(account);
            account.setArchival(true);
            return status;
        });
        Long givenId = account.getId();

        //when
        Exception exception = catchException(() -> underTest.addRole(givenId, AccountRole.EMPLOYEE));

        //then
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
        Account account = buildDefaultAccount();
        account.setAccountRoles(new HashSet<>(Set.of(AccountRole.CLIENT)));
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        Long givenId = account.getId();

        //when
        Exception exception = catchException(() -> underTest.addRole(givenId, AccountRole.CLIENT));

        //then
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
        Account account = buildDefaultAccount();
        account.setAccountRoles(new HashSet<>(Set.of(AccountRole.CLIENT)));
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        Long givenId = account.getId();

        //when
        Exception exception = catchException(() -> underTest.addRole(givenId, AccountRole.GUEST));

        //then
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
        Account account = buildDefaultAccount();
        account.setAccountRoles(new HashSet<>(Set.of(AccountRole.ADMIN)));
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        Long givenId = account.getId();

        //when
        Exception exception = catchException(() -> underTest.addRole(givenId, AccountRole.CLIENT));

        //then
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
        Account account = buildDefaultAccount();
        account.setAccountRoles(new HashSet<>(Set.of(AccountRole.CLIENT)));
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        Long givenId = account.getId();

        //when
        Exception exception = catchException(() -> underTest.addRole(givenId, AccountRole.ADMIN));

        //then
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
        Account account = buildDefaultAccount();
        account.setAccountRoles(new HashSet<>(Set.of(AccountRole.CLIENT, AccountRole.EMPLOYEE)));
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        Long givenId = account.getId();

        //when
        Account result = underTest.removeRole(givenId, AccountRole.CLIENT);

        //then
        assertThat(result.getAccountRoles())
            .hasSize(1)
            .containsExactly(AccountRole.EMPLOYEE);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account with provided id can't be found")
    void removeRole_should_throw_AccountNotFoundException() {
        //given
        Long givenId = 1L;

        //when
        Exception exception = catchException(() -> underTest.removeRole(givenId, AccountRole.CLIENT));

        //then
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
        Account account = buildDefaultAccount();
        account.setAccountRoles(new HashSet<>(Set.of(AccountRole.CLIENT, AccountRole.EMPLOYEE)));
        txTemplate.execute(status -> {
            em.persist(account);
            account.setArchival(true);
            return status;
        });
        Long givenId = account.getId();

        //when
        Exception exception = catchException(() -> underTest.removeRole(givenId, AccountRole.CLIENT));

        //then
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
        Account account = buildDefaultAccount();
        account.setAccountRoles(new HashSet<>(Set.of(AccountRole.CLIENT, AccountRole.EMPLOYEE)));
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        Long givenId = account.getId();

        //when
        Exception exception = catchException(() -> underTest.removeRole(givenId, AccountRole.ADMIN));

        //then
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
        Account account = buildDefaultAccount();
        account.setAccountRoles(new HashSet<>(Set.of(AccountRole.CLIENT)));
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        Long givenId = account.getId();

        //when
        Exception exception = catchException(() -> underTest.removeRole(givenId, AccountRole.CLIENT));

        //then
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
        Account account = buildDefaultAccount();
        account.setAccountRoles(new HashSet<>(Set.of(AccountRole.CLIENT)));
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        Long givenId = account.getId();

        //when
        Account result = underTest.changeRole(givenId, AccountRole.EMPLOYEE);

        //then
        assertThat(result.getAccountRoles())
            .hasSize(1)
            .containsExactly(AccountRole.EMPLOYEE);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account with provided id can't be found")
    void changeRole_should_throw_AccountNotFoundException() {
        //given
        Long givenId = 1L;

        //when
        Exception exception = catchException(() -> underTest.changeRole(givenId, AccountRole.EMPLOYEE));

        //then
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
        Account account = buildDefaultAccount();
        account.setAccountRoles(new HashSet<>(Set.of(AccountRole.CLIENT)));
        txTemplate.execute(status -> {
            em.persist(account);
            account.setArchival(true);
            return status;
        });
        Long givenId = account.getId();

        //when
        Exception exception = catchException(() -> underTest.changeRole(givenId, AccountRole.EMPLOYEE));

        //then
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
        Account account = buildDefaultAccount();
        account.setAccountRoles(new HashSet<>(Set.of(AccountRole.CLIENT, AccountRole.EMPLOYEE)));
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        Long givenId = account.getId();

        //when
        Exception exception = catchException(() -> underTest.changeRole(givenId, AccountRole.EMPLOYEE));

        //then
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
        Account account = buildDefaultAccount();
        account.setAccountRoles(new HashSet<>(Set.of(AccountRole.EMPLOYEE)));
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        Long givenId = account.getId();

        //when
        Exception exception = catchException(() -> underTest.changeRole(givenId, AccountRole.EMPLOYEE));

        //then
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
        Account account = buildDefaultAccount();
        account.setAccountRoles(new HashSet<>(Set.of(AccountRole.EMPLOYEE)));
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });
        Long givenId = account.getId();

        //when
        Exception exception = catchException(() -> underTest.changeRole(givenId, AccountRole.GUEST));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantAssignGuestRoleException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_ROLE_CANT_ASSIGN_GUEST);
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