package pl.lodz.p.edu.unit.service.impl;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.edu.dataaccess.model.Account;
import pl.lodz.p.edu.dataaccess.repository.api.AccountRepository;
import pl.lodz.p.edu.exception.AccountNotFoundException;
import pl.lodz.p.edu.exception.ExceptionMessage;
import pl.lodz.p.edu.logic.service.impl.AccountServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
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
            .isInstanceOf(AccountNotFoundException.class)
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
            .isInstanceOf(AccountNotFoundException.class)
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(ExceptionMessage.ACCOUNT_NOT_FOUND);
    }

    @Test
    @Disabled
    void create() {
    }

    @Test
    @Disabled
    void update() {
    }

    @Test
    @Disabled
    void block() {
    }

    @Test
    @Disabled
    void unblock() {
    }

    @Test
    @Disabled
    void archive() {
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
}