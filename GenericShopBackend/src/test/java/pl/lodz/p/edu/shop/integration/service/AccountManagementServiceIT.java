package pl.lodz.p.edu.shop.integration.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.edu.shop.AccountsModuleTestData;
import pl.lodz.p.edu.shop.config.PostgresqlContainerSetup;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountRole;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountState;
import pl.lodz.p.edu.shop.exception.ExceptionMessage;
import pl.lodz.p.edu.shop.exception.account.*;
import pl.lodz.p.edu.shop.logic.service.api.AccountManagementService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Integration tests for AccountManagementService")
@SpringBootTest
@ActiveProfiles("it")
class AccountManagementServiceIT extends PostgresqlContainerSetup {

    @Autowired
    private AccountManagementService underTest;

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
    @DisplayName("Should return empty list")
    void findAll_positive_1() {
        //given

        //when
        List<Account> result = underTest.findAll();

        //then
        assertThat(result)
            .isEmpty();
    }

    @Test
    @DisplayName("Should return list with elements")
    void findAll_positive_2() {
        //given
        Account[] givenAccounts = {
            AccountsModuleTestData.buildDefaultAccount(),
            AccountsModuleTestData.buildDefaultAccount()
        };
        txTemplate.execute(status -> {
            Arrays.stream(givenAccounts).forEach(account -> em.persist(account));
            return status;
        });

        //when
        List<Account> result = underTest.findAll();

        //then
        assertThat(result)
            .hasSize(2)
            .containsExactly(givenAccounts);
    }


    @Test
    @DisplayName("Should return account if account with id is found")
    void findById_positive_1() {
        //given
        Account givenAccount = AccountsModuleTestData.buildDefaultAccount();
        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });

        Long givenId = givenAccount.getId();

        //when
        Account result = underTest.findById(givenId);

        //then
        assertThat(result)
            .isEqualTo(givenAccount);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException if account with id is not found")
    void findById_negative_1() {
        //given
        Long givenId = 1L;

        //when
        Exception exception = catchException(() -> underTest.findById(givenId));

        //then
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

        //when
        Account result = underTest.create(givenAccount);

        //then
        assertThat(result)
            .isEqualTo(givenAccount);

        txTemplate.execute(status -> {
            List<Account> databaseAccounts = em.createQuery("from Account ", Account.class).getResultList();

            assertThat(databaseAccounts)
                .hasSize(1)
                .containsExactly(givenAccount);
            return status;
        });
    }

    @Test
    @DisplayName("Should throw CantCreateAccountWithManyRolesException when account has more than one role")
    void create_negative_1() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT, AccountRole.EMPLOYEE));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
                .accountRoles(givenRoles)
                .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });

        //when
        Exception exception = catchException(() -> underTest.create(givenAccount));

        //then
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
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.GUEST));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
                .accountRoles(givenRoles)
                .build();

        //when
        Exception exception = catchException(() -> underTest.create(givenAccount));

        //then
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
        Account givenAccount = AccountsModuleTestData.buildDefaultAccount();
        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });

        Account accountWithConflictLogin = AccountsModuleTestData.getDefaultAccountBuilder()
            .login(givenAccount.getLogin())
            .build();

        //when
        Exception exception = catchException(() -> underTest.create(accountWithConflictLogin));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountLoginConflictException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_CONFLICT_LOGIN);
    }

    @Test
    @DisplayName("Should throw AccountEmailConflictException when new Account has same newEmail")
    void create_negative_5() {
        //given
        Account givenAccount = AccountsModuleTestData.buildDefaultAccount();
        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });

        Account accountWithConflictEmail = AccountsModuleTestData.getDefaultAccountBuilder()
            .email(givenAccount.getEmail())
            .build();

        //when
        Exception exception = catchException(() -> underTest.create(accountWithConflictEmail));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(AccountEmailConflictException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_CONFLICT_EMAIL);
    }

    @Test
    @DisplayName("Should block active account")
    void block_positive_1() {
        //given
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountState(AccountState.ACTIVE)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });
        Long givenId = givenAccount.getId();

        //when
        Account result = underTest.block(givenId);

        //then
        assertEquals(AccountState.BLOCKED, result.getAccountState());
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account is not found")
    void block_negative_1() {
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
    void block_negative_2() {
        //given
        Account givenAccount = AccountsModuleTestData.buildDefaultAccount();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            givenAccount.setArchival(true);
            return status;
        });

        Long givenId = givenAccount.getId();

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
    void block_negative_3() {
        //given
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountState(AccountState.NOT_VERIFIED)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });
        Long givenId = givenAccount.getId();

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
    void block_negative_4() {
        //given
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountState(AccountState.BLOCKED)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });
        Long givenId = givenAccount.getId();

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
    void unblock_positive_1() {
        //given
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountState(AccountState.BLOCKED)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });
        Long givenId = givenAccount.getId();

        //when
        Account result = underTest.unblock(givenId);

        //then
        assertEquals(AccountState.ACTIVE, result.getAccountState());
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account can't be found")
    void unblock_negative_1() {
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
    void unblock_negative_2() {
        //given
        Account givenAccount = AccountsModuleTestData.buildDefaultAccount();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            givenAccount.setArchival(true);
            return status;
        });
        Long givenId = givenAccount.getId();

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
    void unblock_negative_3() {
        //given
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountState(AccountState.NOT_VERIFIED)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });
        Long givenId = givenAccount.getId();

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
    void unblock_negative_4() {
        //given
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountState(AccountState.ACTIVE)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });
        Long givenId = givenAccount.getId();

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
    void archive_positive_1() {
        //given
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountState(AccountState.ACTIVE)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });
        Long givenId = givenAccount.getId();

        //when
        Account result = underTest.archive(givenId);

        //then
        assertEquals(true, result.isArchival());
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account can't be found during archive")
    void archive_negative_1() {
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
    void archive_negative_2() {
        //given
        Account givenAccount = AccountsModuleTestData.buildDefaultAccount();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            givenAccount.setArchival(true);
            return status;
        });
        Long givenId = givenAccount.getId();

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
    void addRole_positive_1() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });
        Long givenId = givenAccount.getId();

        //when
        Account result = underTest.addRole(givenId, AccountRole.EMPLOYEE);

        //then
        assertThat(result.getAccountRoles())
            .hasSize(2)
            .contains(AccountRole.CLIENT, AccountRole.EMPLOYEE);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account can't be found")
    void addRole_negative_1() {
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
    void addRole_negative_2() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            givenAccount.setArchival(true);
            return status;
        });
        Long givenId = givenAccount.getId();

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
    void addRole_negative_3() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });
        Long givenId = givenAccount.getId();

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
    void addRole_negative_4() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });
        Long givenId = givenAccount.getId();

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
    void addRole_negative_5() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.ADMIN));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });
        Long givenId = givenAccount.getId();

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
    void addRole_negative_6() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });
        Long givenId = givenAccount.getId();

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
    void removeRole_positive_1() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT, AccountRole.EMPLOYEE));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });
        Long givenId = givenAccount.getId();

        //when
        Account result = underTest.removeRole(givenId, AccountRole.CLIENT);

        //then
        assertThat(result.getAccountRoles())
            .hasSize(1)
            .containsExactly(AccountRole.EMPLOYEE);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account with provided id can't be found")
    void removeRole_negative_1() {
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
    void removeRole_negative_2() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT, AccountRole.EMPLOYEE));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            givenAccount.setArchival(true);
            return status;
        });
        Long givenId = givenAccount.getId();

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
    void removeRole_negative_3() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT, AccountRole.EMPLOYEE));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });
        Long givenId = givenAccount.getId();

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
    void removeRole_negative_4() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });
        Long givenId = givenAccount.getId();

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
    void changeRole_positive_1() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });
        Long givenId = givenAccount.getId();

        //when
        Account result = underTest.changeRole(givenId, AccountRole.EMPLOYEE);

        //then
        assertThat(result.getAccountRoles())
            .hasSize(1)
            .containsExactly(AccountRole.EMPLOYEE);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account with provided id can't be found")
    void changeRole_negative_1() {
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
    void changeRole_negative_2() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            givenAccount.setArchival(true);
            return status;
        });
        Long givenId = givenAccount.getId();

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
    void changeRole_negative_3() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.CLIENT, AccountRole.EMPLOYEE));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });
        Long givenId = givenAccount.getId();

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
    void changeRole_negative_4() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.EMPLOYEE));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });
        Long givenId = givenAccount.getId();

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
    void changeRole_negative_5() {
        //given
        HashSet<AccountRole> givenRoles = new HashSet<>(Set.of(AccountRole.EMPLOYEE));
        Account givenAccount = AccountsModuleTestData.getDefaultAccountBuilder()
            .accountRoles(givenRoles)
            .build();

        txTemplate.execute(status -> {
            em.persist(givenAccount);
            return status;
        });
        Long givenId = givenAccount.getId();

        //when
        Exception exception = catchException(() -> underTest.changeRole(givenId, AccountRole.GUEST));

        //then
        assertThat(exception)
            .isNotNull()
            .isExactlyInstanceOf(CantAssignGuestRoleException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_ROLE_CANT_ASSIGN_GUEST);
    }
}