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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import pl.lodz.p.edu.TestData;
import pl.lodz.p.edu.dataaccess.model.Account;
import pl.lodz.p.edu.exception.ExceptionMessage;
import pl.lodz.p.edu.presentation.dto.user.account.AccountCreateDto;
import pl.lodz.p.edu.presentation.dto.user.account.AccountOutputDto;
import pl.lodz.p.edu.presentation.mapper.AccountMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        TestData.resetCounter();;
    }

    @Test
    @DisplayName("Should return response with status 200 and body with empty list")
    void getAll_should_return_status_ok_with_empty_list() throws Exception {
        //given
        String expectedResult = objectMapper.writeValueAsString(new ArrayList<>());

        //when
        ResultActions resultActions = mockMvc.perform(get("/account"));

        //then
        resultActions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(expectedResult));
    }

    @Test
    @DisplayName("Should return response with status 200 and body with account in list")
    void getAll_should_return_status_ok_with_account_in_list() throws Exception {
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
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(expectedResult));
    }

    @Test
    @DisplayName("Should return response with status 200 and body with account in list")
    void getById_should_return_status_ok_with_account() throws Exception {
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
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(expectedResult));
    }

    @Test
    @DisplayName("Should return response with status 404 and body with exception message when account can't be found")
    void getById_should_return_status_not_found_with_exception_message() throws Exception {
        //given
        String expectedExceptionCode = ExceptionMessage.ACCOUNT_NOT_FOUND;
        Long givenId = 1L;

        //when
        ResultActions resultActions = mockMvc.perform(get("/account/id/%d".formatted(givenId)));

        //then
        resultActions.andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message", is(expectedExceptionCode)));
    }

    @Test
    @DisplayName("Should return response with status 201 and body with created account")
    void createAccount_should_return_status_created_with_created_account() throws Exception {
        //given
        AccountCreateDto accountCreateDto = TestData.buildDefaultAccountCreateDto();
        String inputData = objectMapper.writeValueAsString(accountCreateDto);

        //when
        MockHttpServletRequestBuilder postRequest = post("/account")
            .contentType(MediaType.APPLICATION_JSON)
            .content(inputData);

        ResultActions resultActions = mockMvc.perform(postRequest);

        //then
        String location = resultActions.andReturn().getResponse().getHeader("Location");
        Pattern pattern = Pattern.compile("/id/(\\d+)");

        assertThat(location)
            .isNotBlank()
            .matches(pattern);

        Matcher matcher = pattern.matcher(location);
        assertThat(matcher.find()).isTrue();
        Long accountId = Long.parseLong(matcher.group(1));

        Account account = em.find(Account.class, accountId);
        AccountOutputDto accountOutputDto = accountMapper.mapToAccountOutputDto(account);
        String expectedResult = objectMapper.writeValueAsString(accountOutputDto);

        resultActions.andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(expectedResult));

    }

    @Test
    @DisplayName("Should return response with status 400 and body with exception message when provided login is null")
    void createAccount_should_return_status_bad_request_when_login_is_null() throws Exception {
        //given
        AccountCreateDto accountWithDuplicateLogin = TestData.getDefaultAccountCreateDtoBuilder()
            .login(null)
            .build();
        String givenRequestBody = objectMapper.writeValueAsString(accountWithDuplicateLogin);

        //when
        MockHttpServletRequestBuilder postRequest = post("/account")
            .contentType(MediaType.APPLICATION_JSON)
            .content(givenRequestBody);
        ResultActions resultActions = mockMvc.perform(postRequest);

        //then
        resultActions.andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.messages.length()", is(1)))
            .andExpect(jsonPath("$.messages.login[0]", is(ExceptionMessage.Validation.FIELD_NOT_NULL)));
    }

    @Test
    @DisplayName("Should return response with status 409 and body with exception message when there is already account with given login")
    void createAccount_should_return_status_conflict_with_exception_message_containing_duplicate_login_message() throws Exception {
        //given
        Account account = TestData.buildDefaultAccount();
        String givenLogin = "Login";
        account.setLogin(givenLogin);
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });

        AccountCreateDto accountWithDuplicateLogin = TestData.getDefaultAccountCreateDtoBuilder()
            .login(givenLogin)
            .build();
        String givenRequestBody = objectMapper.writeValueAsString(accountWithDuplicateLogin);

        //when
        MockHttpServletRequestBuilder postRequest = post("/account")
            .contentType(MediaType.APPLICATION_JSON)
            .content(givenRequestBody);
        ResultActions resultActions = mockMvc.perform(postRequest);

        //then
        resultActions.andDo(print())
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message", is(ExceptionMessage.ACCOUNT_CONFLICT_LOGIN)));
    }

    @Test
    @DisplayName("Should return response with status 409 and body with exception message when there is already account with given email")
    void createAccount_should_return_status_conflict_with_exception_message_containing_duplicate_email_message() throws Exception {
        //given
        Account account = TestData.buildDefaultAccount();
        String givenEmail = "example@example.com";
        account.setEmail(givenEmail);
        txTemplate.execute(status -> {
            em.persist(account);
            return status;
        });

        AccountCreateDto accountWithDuplicateLogin = TestData.getDefaultAccountCreateDtoBuilder()
            .email(givenEmail)
            .build();
        String givenRequestBody = objectMapper.writeValueAsString(accountWithDuplicateLogin);

        //when
        MockHttpServletRequestBuilder postRequest = post("/account")
            .contentType(MediaType.APPLICATION_JSON)
            .content(givenRequestBody);
        ResultActions resultActions = mockMvc.perform(postRequest);

        //then
        resultActions.andDo(print())
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message", is(ExceptionMessage.ACCOUNT_CONFLICT_EMAIL)));
    }
}
