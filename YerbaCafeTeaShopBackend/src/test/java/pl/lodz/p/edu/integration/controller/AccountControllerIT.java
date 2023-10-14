package pl.lodz.p.edu.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import pl.lodz.p.edu.dataaccess.model.Account;
import pl.lodz.p.edu.TestData;
import pl.lodz.p.edu.exception.ExceptionMessage;
import pl.lodz.p.edu.presentation.dto.user.account.AccountOutputDto;
import pl.lodz.p.edu.presentation.mapper.AccountMapper;

import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Integration tests for AccountController")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("it")
public class AccountControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private PlatformTransactionManager txManager;

    @Autowired
    private EntityManager em;

    private TransactionTemplate txTemplate;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        txTemplate = new TransactionTemplate(txManager);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @AfterEach
    void tearDown() {
        txTemplate.execute(status -> {
            em.createQuery("DELETE FROM Account ").executeUpdate();
            em.createQuery("DELETE FROM Person ").executeUpdate();
            em.createQuery("DELETE FROM Address ").executeUpdate();
            return status;
        });
    }

    @Test
    @DisplayName("Should return response with status 200 and body with empty list")
    void getAll_should_return_ok_with_empty_list() throws Exception {
        //given
        String expectedResult = objectMapper.writeValueAsString(new ArrayList<>());

        //when
        ResultActions resultActions = mockMvc.perform(get("/account"));

        //then
        resultActions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResult));
    }

    @Test
    @DisplayName("Should return response with status 200 and body with account in list")
    void getAll_should_return_ok_with_account_in_list() throws Exception {
        //given
        Account account = TestData.buildDefaultAccount();
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });

        AccountOutputDto accountOutputDto = accountMapper.mapToAccountOutputDto(account);
        String expectedResult = objectMapper.writeValueAsString(Collections.singletonList(accountOutputDto));

        //when
        ResultActions resultActions = mockMvc.perform(get("/account"));

        //then
        resultActions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResult));
    }

    @Test
    @DisplayName("Should return response with status 200 and body with account in list")
    void getById_should_return_ok_with_account() throws Exception {
        //given
        Account account = TestData.buildDefaultAccount();
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });

        AccountOutputDto accountOutputDto = accountMapper.mapToAccountOutputDto(account);
        String expectedResult = objectMapper.writeValueAsString(accountOutputDto);

        //when
        ResultActions resultActions = mockMvc.perform(get("/account/id/%d".formatted(account.getId())));

        //then
        resultActions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResult));
    }

    @Test
    @DisplayName("Should return response with status 404 and body with exception message when account can't be found")
    void getById_should_return_not_found_with_exception_message() throws Exception {
        //given
        String expectedExceptionCode = ExceptionMessage.ACCOUNT_NOT_FOUND;
        Long givenId = 1L;

        //when
        ResultActions resultActions = mockMvc.perform(get("/account/id/%d".formatted(givenId)));

        //then
        resultActions.andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", is(expectedExceptionCode)));
    }
}
