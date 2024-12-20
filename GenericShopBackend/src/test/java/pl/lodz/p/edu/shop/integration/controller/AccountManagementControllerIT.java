package pl.lodz.p.edu.shop.integration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import pl.lodz.p.edu.shop.AccountsModuleTestData;
import pl.lodz.p.edu.shop.config.PostgresqlContainerSetup;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountState;
import pl.lodz.p.edu.shop.exception.ExceptionMessage;
import pl.lodz.p.edu.shop.presentation.controller.ApiRoot;
import pl.lodz.p.edu.shop.presentation.dto.user.account.AccountOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.CreateAccountDto;
import pl.lodz.p.edu.shop.presentation.mapper.api.AccountMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Integration tests for AccountManagementController")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("it")
public class AccountManagementControllerIT extends PostgresqlContainerSetup {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    @Qualifier("accountsModTxManager")
    private PlatformTransactionManager txManager;

    @Autowired
    @Qualifier("accountsModEmFactory")
    private EntityManager em;

    private TransactionTemplate txTemplate;

    private ObjectMapper objectMapper;

    private final String BASE_API = "%s/accounts".formatted(ApiRoot.API_ROOT);

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
            em.createQuery("DELETE FROM Contact ").executeUpdate();
            em.createQuery("DELETE FROM Address ").executeUpdate();
            return status;
        });

        AccountsModuleTestData.resetCounter();;
    }

    @Nested
    @DisplayName("GET getAll()")
    class GetAll {

        @Nested
        @DisplayName("Positive")
        class Positive {

            @Test
            @DisplayName("Should return response with status 200 and body with empty list")
            void getAll_should_return_status_ok_with_empty_list() throws Exception {
                //given
                String expectedResult = objectMapper.writeValueAsString(new ArrayList<>());

                //when
                ResultActions resultActions = mockMvc.perform(get(BASE_API));

                //then
                resultActions.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").value(asParsedJson(expectedResult)));
            }

            @Test
            @DisplayName("Should return response with status 200 and body with account in list")
            void getAll_should_return_status_ok_with_account_in_list() throws Exception {
                //given
                Account account = AccountsModuleTestData.buildDefaultAccount();
                txTemplate.execute(status -> {
                    em.persist(account);
                    return status;
                });

                AccountOutputDto accountOutputDto = accountMapper.mapToAccountOutputDtoWithoutVersion(account);
                String expectedResult = objectMapper.writeValueAsString(Collections.singletonList(accountOutputDto));

                //when
                ResultActions resultActions = mockMvc.perform(get(BASE_API));

                //then
                resultActions.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").value(asParsedJson(expectedResult)))
                    .andExpect(jsonPath("$.totalElements").value(1));
            }
        }

        @Nested
        @DisplayName("Negative")
        class Negative {

        }
    }

    @Nested
    @DisplayName("GET getById()")
    class GetById {

        @Nested
        @DisplayName("Positive")
        class Positive {

            @Test
            @DisplayName("Should return response with status 200 and body with account in page")
            void getById_should_return_status_ok_with_account() throws Exception {
                //given
                Account account = AccountsModuleTestData.buildDefaultAccount();
                txTemplate.execute(status -> {
                    em.persist(account);
                    return status;
                });

                AccountOutputDto accountOutputDto = accountMapper.mapToAccountOutputDtoWithoutVersion(account);
                String expectedResult = objectMapper.writeValueAsString(accountOutputDto);

                //when
                ResultActions resultActions = mockMvc.perform(get("%s/id/%d".formatted(BASE_API, account.getId())));

                //then
                resultActions.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(expectedResult));
            }
        }

        @Nested
        @DisplayName("Negative")
        class Negative {

            @Test
            @DisplayName("Should return response with status 404 and body with exception message when account can't be found")
            void getById_should_return_status_not_found_with_exception_message() throws Exception {
                //given
                String expectedExceptionCode = ExceptionMessage.ACCOUNT_NOT_FOUND;
                Long givenId = 1L;

                //when
                ResultActions resultActions = mockMvc.perform(get("%s/id/%d".formatted(BASE_API, givenId)));

                //then
                resultActions.andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message", is(expectedExceptionCode)));
            }
        }
    }

    @Nested
    @DisplayName("POST createAccount()")
    class CreateAccount {

        @Nested
        @DisplayName("Positive")
        class Positive {

            @Test
            @DisplayName("Should return response with status 201 and body with created account")
            void createAccount_should_return_status_created_with_created_account() throws Exception {
                //given
                CreateAccountDto createAccountDto = AccountsModuleTestData.buildDefaultAccountCreateDto();
                String inputData = objectMapper.writeValueAsString(createAccountDto);

                //when
                MockHttpServletRequestBuilder postRequest = post(BASE_API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(inputData);

                ResultActions resultActions = mockMvc.perform(postRequest);

                //then
                resultActions.andDo(print())
                    .andExpect(status().isCreated());

                String location = resultActions.andReturn().getResponse().getHeader("Location");
                Pattern pattern = Pattern.compile("/id/(\\d+)");

                assertThat(location)
                    .isNotBlank()
                    .matches(pattern);

                Matcher matcher = pattern.matcher(location);
                assertThat(matcher.find()).isTrue();
                Long accountId = Long.parseLong(matcher.group(1));

                Account account = em.find(Account.class, accountId);
                AccountOutputDto accountOutputDto = accountMapper.mapToAccountOutputDtoWithoutVersion(account);
                String expectedResult = objectMapper.writeValueAsString(accountOutputDto);

                resultActions.andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(expectedResult));
            }
        }

        @Nested
        @DisplayName("Negative")
        class Negative {

            @Nested
            @DisplayName("Validation tests")
            class Validation {

                @Nested
                @DisplayName("Login field")
                class Login {

                    @ParameterizedTest
                    @NullSource
                    @DisplayName("Should return response with status 400 and body with exception message when provided login is null")
                    void createAccount_should_return_status_bad_request_when_login_is_null(String givenLogin) throws Exception {
                        //given
                        CreateAccountDto accountWithNullLogin = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .login(givenLogin)
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithNullLogin);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages.login", hasItem(ExceptionMessage.Validation.NOT_NULL)));
                    }

                    @ParameterizedTest
                    @ValueSource(strings = {" ", "log-in", "1login"})
                    @DisplayName("Should return response with status 400 and body with exception message when provided login is not capitalized")
                    void createAccount_should_return_status_bad_request_when_login_is_not_capitalized(String givenLogin) throws Exception {
                        //given
                        CreateAccountDto accountWithGivenLogin = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .login(givenLogin)
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithGivenLogin);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages.login", hasItem(ExceptionMessage.Validation.LOGIN_WRONG)));
                    }

                    @ParameterizedTest
                    @ValueSource(strings = {"", "TooLongNameForLogin12"})
                    @DisplayName("Should return response with status 400 and body with exception message when provided login is too short or too long")
                    void createAccount_should_return_status_bad_request_when_login_has_wrong_size(String givenLogin) throws Exception {
                        //given
                        CreateAccountDto accountWithGivenLogin = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .login(givenLogin)
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithGivenLogin);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages.login", hasItem(ExceptionMessage.Validation.SIZE)));
                    }
                }

                @Nested
                @DisplayName("Email field")
                class Email {

                    @ParameterizedTest
                    @NullSource
                    @DisplayName("Should return response with status 400 and body with exception message when provided newEmail is null")
                    void createAccount_should_return_status_bad_request_when_email_is_null(String givenEmail) throws Exception {
                        //given
                        CreateAccountDto accountWithNullEmail = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .email(givenEmail)
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithNullEmail);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages.email", hasItem(ExceptionMessage.Validation.NOT_NULL)));
                    }

                    @ParameterizedTest
                    @ValueSource(strings = {" ", "newEmail", "newEmail@"})
                    @DisplayName("Should return response with status 400 and body with exception message when provided newEmail is not valid")
                    void createAccount_should_return_status_bad_request_when_email_is_not_valid(String givenEmail) throws Exception {
                        //given
                        CreateAccountDto accountWithGivenEmail = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .email(givenEmail)
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithGivenEmail);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages.email", hasItem(ExceptionMessage.Validation.EMAIL)));
                    }
                }

                @Nested
                @DisplayName("Password field")
                class Password {

                    @ParameterizedTest
                    @NullSource
                    @DisplayName("Should return response with status 400 and body with exception message when provided password is null")
                    void createAccount_should_return_status_bad_request_when_password_is_null(String givenPassword) throws Exception {
                        //given
                        CreateAccountDto accountWithNullPassword = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .password(givenPassword)
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithNullPassword);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages.password", hasItem(ExceptionMessage.Validation.NOT_NULL)));
                    }

                    @ParameterizedTest
                    @ValueSource(strings = {" ", "password", "Password", "password!", "!password"})
                    @DisplayName("Should return response with status 400 and body with exception message when provided password is not valid password")
                    void createAccount_should_return_status_bad_request_when_password_is_not_valid(String givenPassword) throws Exception {
                        //given
                        CreateAccountDto accountWithGivenPassword = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .password(givenPassword)
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithGivenPassword);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages.password", hasItem(ExceptionMessage.Validation.PASSWORD_WRONG)));
                    }

                    @ParameterizedTest
                    @ValueSource(strings = {"", "Passwd!", "LongPassword!WithMaxLength12345"})
                    @DisplayName("Should return response with status 400 and body with exception message when provided password is too short or too long")
                    void createAccount_should_return_status_bad_request_when_password_has_wrong_size(String givenPassword) throws Exception {
                        //given
                        CreateAccountDto accountWithGivenPassword = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .password(givenPassword)
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithGivenPassword);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages.password", hasItem(ExceptionMessage.Validation.PASSWORD_WRONG_SIZE)));
                    }
                }

                @Nested
                @DisplayName("Locale field")
                class Locale {

                    @ParameterizedTest
                    @ValueSource(strings = " ")
                    @NullSource
                    @DisplayName("Should return response with status 400 and body with exception message when provided locale is blank")
                    void createAccount_should_return_status_bad_request_when_locale_is_blank(String givenLocale) throws Exception {
                        //given
                        CreateAccountDto accountWithNullLocale = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .locale(givenLocale)
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithNullLocale);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages.locale", hasItem(ExceptionMessage.Validation.BLANK)));
                    }

                    @ParameterizedTest
                    @ValueSource(strings = {"adsfdasf", "123", "!!", "fr"})
                    @DisplayName("Should return response with status 400 and body with exception message when provided locale is not supported")
                    void createAccount_should_return_status_bad_request_when_locale_is_not_supported(String givenLocale) throws Exception {
                        //given
                        CreateAccountDto accountWithGivenLocale = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .locale(givenLocale)
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithGivenLocale);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages.locale", hasItem(ExceptionMessage.Validation.ACCOUNT_LOCALE_NOT_SUPPORTED)));
                    }
                }

                @Nested
                @DisplayName("First name field")
                class FirstName {

                    @ParameterizedTest
                    @NullSource
                    @DisplayName("Should return response with status 400 and body with exception message when provided firstName is null")
                    void createAccount_should_return_status_bad_request_when_firstName_is_null(String givenFirstName) throws Exception {
                        //given
                        CreateAccountDto accountWithNullFirstName = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .firstName(givenFirstName)
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithNullFirstName);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages.firstName", hasItem(ExceptionMessage.Validation.NOT_NULL)));
                    }

                    @ParameterizedTest
                    @ValueSource(strings = {" ", "john", "1john", "John1"})
                    @DisplayName("Should return response with status 400 and body with exception message when provided firstName is not capitalized")
                    void createAccount_should_return_status_bad_request_when_firstName_is_not_capitalized(String givenFirstName) throws Exception {
                        //given
                        CreateAccountDto accountWithGivenFirstName = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .firstName(givenFirstName)
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithGivenFirstName);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages.firstName", hasItem(ExceptionMessage.Validation.CAPITALIZED)));
                    }

                    @ParameterizedTest
                    @ValueSource(strings = {"", "TooLongNameForName123"})
                    @DisplayName("Should return response with status 400 and body with exception message when provided firstName is too short or too long")
                    void createAccount_should_return_status_bad_request_when_firstName_has_wrong_size(String givenFirstName) throws Exception {
                        //given
                        CreateAccountDto accountWithGivenFirstName = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .firstName(givenFirstName)
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithGivenFirstName);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages.firstName", hasItem(ExceptionMessage.Validation.SIZE)));
                    }
                }

                @Nested
                @DisplayName("Last name field")
                class LastName {

                    @ParameterizedTest
                    @NullSource
                    @DisplayName("Should return response with status 400 and body with exception message when provided lastName is null")
                    void createAccount_should_return_status_bad_request_when_lastName_is_null(String givenLastName) throws Exception {
                        //given
                        CreateAccountDto accountWithNullLastName = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .lastName(givenLastName)
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithNullLastName);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages.lastName", hasItem(ExceptionMessage.Validation.NOT_NULL)));
                    }

                    @ParameterizedTest
                    @ValueSource(strings = {" ", "doe", "1doe", "Doe1"})
                    @DisplayName("Should return response with status 400 and body with exception message when provided lastName is not capitalized")
                    void createAccount_should_return_status_bad_request_when_lastName_is_not_capitalized(String givenLastName) throws Exception {
                        //given
                        CreateAccountDto accountWithGivenLastName = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .lastName(givenLastName)
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithGivenLastName);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages.lastName", hasItem(ExceptionMessage.Validation.CAPITALIZED)));
                    }

                    @ParameterizedTest
                    @ValueSource(strings = {"", "TooLongNameForSurname"})
                    @DisplayName("Should return response with status 400 and body with exception message when provided lastName is too short or too long")
                    void createAccount_should_return_status_bad_request_when_lastName_has_wrong_size(String givenLastName) throws Exception {
                        //given
                        CreateAccountDto accountWithGivenLastName = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .lastName(givenLastName)
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithGivenLastName);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages.lastName", hasItem(ExceptionMessage.Validation.SIZE)));
                    }
                }

                @Nested
                @DisplayName("Address field")
                class Address {

                    @ParameterizedTest
                    @NullSource
                    @DisplayName("Should return response with status 400 and body with exception message when provided postalCode is null")
                    void createAccount_should_return_status_bad_request_when_postalCode_is_null(String givenPostalCode) throws Exception {
                        //given
                        CreateAccountDto accountWithNullPostalCode = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .address(AccountsModuleTestData.getDefaultAddressCreateDtoBuilder().postalCode(givenPostalCode).build())
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithNullPostalCode);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages['address.postalCode']", hasItem(ExceptionMessage.Validation.NOT_NULL)));
                    }

                    @ParameterizedTest
                    @ValueSource(strings = {" ", "333-22", "aa-200", "200-aa"})
                    @DisplayName("Should return response with status 400 and body with exception message when provided postalCode is not in format dd-ddd")
                    void createAccount_should_return_status_bad_request_when_postalCode_is_not_in_format(String givenPostalCode) throws Exception {
                        //given
                        CreateAccountDto accountWithGivenPostalCode = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .address(AccountsModuleTestData.getDefaultAddressCreateDtoBuilder().postalCode(givenPostalCode).build())
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithGivenPostalCode);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages['address.postalCode']", hasItem(ExceptionMessage.Validation.ACCOUNT_POSTAL_CODE_WRONG)));
                    }

                    @ParameterizedTest
                    @NullSource
                    @DisplayName("Should return response with status 400 and body with exception message when provided country is null")
                    void createAccount_should_return_status_bad_request_when_country_is_null(String givenCountry) throws Exception {
                        //given
                        CreateAccountDto accountWithNullCountry = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .address(AccountsModuleTestData.getDefaultAddressCreateDtoBuilder().country(givenCountry).build())
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithNullCountry);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages['address.country']", hasItem(ExceptionMessage.Validation.NOT_NULL)));
                    }

                    @ParameterizedTest
                    @ValueSource(strings = {" ", "poland", "Poland100", "Poland_"})
                    @DisplayName("Should return response with status 400 and body with exception message when provided country is not capitalized")
                    void createAccount_should_return_status_bad_request_when_country_is_not_capitalized(String givenCountry) throws Exception {
                        //given
                        CreateAccountDto accountWithGivenCountry = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .address(AccountsModuleTestData.getDefaultAddressCreateDtoBuilder().country(givenCountry).build())
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithGivenCountry);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages['address.country']", hasItem(ExceptionMessage.Validation.CAPITALIZED)));
                    }

                    @ParameterizedTest
                    @NullSource
                    @DisplayName("Should return response with status 400 and body with exception message when provided city is null")
                    void createAccount_should_return_status_bad_request_when_city_is_null(String givenCity) throws Exception {
                        //given
                        CreateAccountDto accountWithNullCity = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .address(AccountsModuleTestData.getDefaultAddressCreateDtoBuilder().city(givenCity).build())
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithNullCity);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages['address.city']", hasItem(ExceptionMessage.Validation.NOT_NULL)));
                    }

                    @ParameterizedTest
                    @ValueSource(strings = {" ", "london", "London200", "London_"})
                    @DisplayName("Should return response with status 400 and body with exception message when provided city is not capitalized")
                    void createAccount_should_return_status_bad_request_when_city_is_not_capitalized(String givenCity) throws Exception {
                        //given
                        CreateAccountDto accountWithGivenCity = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .address(AccountsModuleTestData.getDefaultAddressCreateDtoBuilder().city(givenCity).build())
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithGivenCity);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages['address.city']", hasItem(ExceptionMessage.Validation.CAPITALIZED)));
                    }

                    @ParameterizedTest
                    @NullSource
                    @DisplayName("Should return response with status 400 and body with exception message when provided street is null")
                    void createAccount_should_return_status_bad_request_when_street_is_null(String givenStreet) throws Exception {
                        //given
                        CreateAccountDto accountWithNullStreet = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .address(AccountsModuleTestData.getDefaultAddressCreateDtoBuilder().street(givenStreet).build())
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithNullStreet);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages['address.street']", hasItem(ExceptionMessage.Validation.NOT_NULL)));
                    }

                    @ParameterizedTest
                    @ValueSource(strings = {" ", "road", "Road1", "Road_"})
                    @DisplayName("Should return response with status 400 and body with exception message when provided street is not capitalized")
                    void createAccount_should_return_status_bad_request_when_street_is_not_capitalized(String givenStreet) throws Exception {
                        //given
                        CreateAccountDto accountWithGivenStreet = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .address(AccountsModuleTestData.getDefaultAddressCreateDtoBuilder().street(givenStreet).build())
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithGivenStreet);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages['address.street']", hasItem(ExceptionMessage.Validation.CAPITALIZED)));
                    }

                    @ParameterizedTest
                    @NullSource
                    @DisplayName("Should return response with status 400 and body with exception message when provided houseNumber is null")
                    void createAccount_should_return_status_bad_request_when_houseNumber_is_null(Integer givenHouseNumber) throws Exception {
                        //given
                        CreateAccountDto accountWithNullHouseNumber = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .address(AccountsModuleTestData.getDefaultAddressCreateDtoBuilder().houseNumber(givenHouseNumber).build())
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithNullHouseNumber);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages['address.houseNumber']", hasItem(ExceptionMessage.Validation.NOT_NULL)));
                    }

                    @ParameterizedTest
                    @ValueSource(ints = {-1, 0})
                    @DisplayName("Should return response with status 400 and body with exception message when provided houseNumber is not positive")
                    void createAccount_should_return_status_bad_request_when_street_is_not_capitalized(Integer givenHouseNumber) throws Exception {
                        //given
                        CreateAccountDto accountWithNotPositiveHouseNumber = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .address(AccountsModuleTestData.getDefaultAddressCreateDtoBuilder().houseNumber(givenHouseNumber).build())
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithNotPositiveHouseNumber);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages['address.houseNumber']", hasItem(ExceptionMessage.Validation.POSITIVE)));
                    }
                }

                @Nested
                @DisplayName("Account accountState")
                class AccountState {

                    @ParameterizedTest
                    @ValueSource(strings = " ")
                    @NullSource
                    @DisplayName("Should return response with status 400 and body with exception message when provided accountState is blank")
                    void createAccount_should_return_status_bad_request_when_accountState_is_blank(String givenAccountState) throws Exception {
                        //given
                        CreateAccountDto accountWithBlankAccountState = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .accountState(givenAccountState)
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithBlankAccountState);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages.accountState", hasItem(ExceptionMessage.Validation.BLANK)));
                    }

                    @ParameterizedTest
                    @ValueSource(strings = {"adsfdasf"})
                    @DisplayName("Should return response with status 400 and body with exception message when provided accountState is not supported")
                    void createAccount_should_return_status_bad_request_when_accountState_is_not_supported(String givenAccountState) throws Exception {
                        //given
                        CreateAccountDto accountWithGivenAccountState = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .accountState(givenAccountState)
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithGivenAccountState);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages.accountState", hasItem(ExceptionMessage.Validation.ACCOUNT_STATE_NOT_SUPPORTED)));
                    }
                }

                @Nested
                @DisplayName("Account role")
                class AccountRole {

                    @ParameterizedTest
                    @ValueSource(strings = " ")
                    @NullSource
                    @DisplayName("Should return response with status 400 and body with exception message when provided accountRole is blank")
                    void createAccount_should_return_status_bad_request_when_accountRole_is_blank(String givenAccountRole) throws Exception {
                        //given
                        CreateAccountDto accountWithBlankAccountRole = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .role(givenAccountRole)
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithBlankAccountRole);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages.role", hasItem(ExceptionMessage.Validation.BLANK)));
                    }

                    @ParameterizedTest
                    @ValueSource(strings = {"adsfdasf", "123", "!!", "fr"})
                    @DisplayName("Should return response with status 400 and body with exception message when provided accountRole is not supported")
                    void createAccount_should_return_status_bad_request_when_accountRole_is_not_supported(String givenAccountRole) throws Exception {
                        //given
                        CreateAccountDto accountWithGivenAccountRole = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                            .role(givenAccountRole)
                            .build();
                        String givenRequestBody = objectMapper.writeValueAsString(accountWithGivenAccountRole);

                        //when
                        MockHttpServletRequestBuilder postRequest = post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(givenRequestBody);
                        ResultActions resultActions = mockMvc.perform(postRequest);

                        //then
                        resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$.messages.length()", is(1)))
                            .andExpect(jsonPath("$.messages.role", hasItem(ExceptionMessage.Validation.ACCOUNT_ROLE_NOT_SUPPORTED)));
                    }
                }
            }

            @ParameterizedTest
            @ValueSource(strings = {"guest", "GUEST", "Guest"})
            @DisplayName("Should return response with status 400 and body with exception message when given role is GUEST")
            void createAccount_should_return_status_bad_request_when_given_role_is_GUEST(String role) throws Exception {
                //given
                CreateAccountDto createAccountDto = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                    .role(role)
                    .build();
                String inputData = objectMapper.writeValueAsString(createAccountDto);

                //when
                MockHttpServletRequestBuilder postRequest = post(BASE_API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(inputData);

                ResultActions resultActions = mockMvc.perform(postRequest);

                //then
                resultActions.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message", is(ExceptionMessage.ACCOUNT_ROLE_CANT_ASSIGN_GUEST)));
            }

            @ParameterizedTest
            @ValueSource(strings = {"not_verified", "NOT_VERIFIED", "Not_verified", "Not_VERIFIED"})
            @DisplayName("Should return response with status 400 and body with exception message when given account accountState is NOT_VERIFIED")
            void createAccount_should_return_bad_request_when_given_state_is_NOT_VERIFIED(String status) throws Exception {
                //given
                CreateAccountDto accountWithNotVerifiedStatus = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                    .accountState(status)
                    .build();
                String inputData = objectMapper.writeValueAsString(accountWithNotVerifiedStatus);

                //when
                MockHttpServletRequestBuilder postRequest = post(BASE_API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(inputData);

                ResultActions resultActions = mockMvc.perform(postRequest);

                //then
                resultActions.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message", is(ExceptionMessage.ACCOUNT_CREATE_CANT_ASSIGN_NOT_VERIFIED)));
            }

            @Test
            @DisplayName("Should return response with status 409 and body with exception message when there is already account with given login")
            void createAccount_should_return_status_conflict_with_exception_message_containing_duplicate_login_message() throws Exception {
                //given
                Account account = AccountsModuleTestData.buildDefaultAccount();
                String givenLogin = "Login";
                account.setLogin(givenLogin);
                txTemplate.execute(status -> {
                    em.persist(account);
                    return status;
                });

                CreateAccountDto accountWithDuplicateLogin = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                    .login(givenLogin)
                    .build();
                String givenRequestBody = objectMapper.writeValueAsString(accountWithDuplicateLogin);

                //when
                MockHttpServletRequestBuilder postRequest = post(BASE_API)
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
            @DisplayName("Should return response with status 409 and body with exception message when there is already account with given newEmail")
            void createAccount_should_return_status_conflict_with_exception_message_containing_duplicate_email_message() throws Exception {
                //given
                Account account = AccountsModuleTestData.buildDefaultAccount();
                String givenEmail = "example@example.com";
                account.setEmail(givenEmail);
                txTemplate.execute(status -> {
                    em.persist(account);
                    return status;
                });

                CreateAccountDto accountWithDuplicateLogin = AccountsModuleTestData.getDefaultAccountCreateDtoBuilder()
                    .email(givenEmail)
                    .build();
                String givenRequestBody = objectMapper.writeValueAsString(accountWithDuplicateLogin);

                //when
                MockHttpServletRequestBuilder postRequest = post(BASE_API)
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
    }

    @Nested
    @DisplayName("PUT blockAccount()")
    class BlockAccount {

        @Nested
        @DisplayName("Positive")
        class Positive {

            @Test
            @DisplayName("Should return response with status 200 with body containing account with status BLOCKED")
            void blockAccount_should_return_status_ok_with_blocked_account() throws Exception {
                //given
                Account account = AccountsModuleTestData.buildDefaultAccount();
                account.setAccountState(AccountState.ACTIVE);

                txTemplate.execute(status -> {
                    em.persist(account);
                    return status;
                });

                //when
                ResultActions resultActions = mockMvc.perform(put("%s/id/%d/block".formatted(BASE_API, account.getId())));

                //then
                resultActions.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.accountState", is("BLOCKED")));
            }
        }

        @Nested
        @DisplayName("Negative")
        class Negative {

            @Test
            @DisplayName("Should return response with status 404 with body containing exception message")
            void blockAccount_should_return_status_not_found() throws Exception {
                //given

                //when
                ResultActions resultActions = mockMvc.perform(put("%s/id/%d/block".formatted(BASE_API, 1L)));

                //then
                resultActions.andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message", is(ExceptionMessage.ACCOUNT_NOT_FOUND)));
            }

            @Test
            @DisplayName("Should return response with status 400 when account is archival")
            void blockAccount_should_return_status_bad_request_when_account_is_archival() throws Exception {
                //given
                Account account = AccountsModuleTestData.buildDefaultAccount();
                account.setAccountState(AccountState.ACTIVE);

                txTemplate.execute(status -> {
                    em.persist(account);
                    account.setArchival(true);
                    return status;
                });

                //when
                ResultActions resultActions = mockMvc.perform(put("%s/id/%d/block".formatted(BASE_API, account.getId())));

                //then
                resultActions.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message", is(ExceptionMessage.ACCOUNT_ARCHIVAL)));
            }

            @ParameterizedTest
            @EnumSource(value = AccountState.class, names = {"BLOCKED", "NOT_VERIFIED"})
            @DisplayName("Should return response with status 400 when account is not ACTIVE")
            void blockAccount_should_return_status_bad_request_when_account_is_not_ACTIVE(AccountState accountState) throws Exception {
                //given
                Account account = AccountsModuleTestData.buildDefaultAccount();
                account.setAccountState(accountState);

                txTemplate.execute(status -> {
                    em.persist(account);
                    return status;
                });

                //when
                ResultActions resultActions = mockMvc.perform(put("%s/id/%d/block".formatted(BASE_API, account.getId())));

                //then
                resultActions.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message", is(ExceptionMessage.ACCOUNT_NOT_ACTIVE)));
            }
        }
    }

    @Nested
    @DisplayName("PUT unblockAccount()")
    class UnblockAccount {

        @Nested
        @DisplayName("Positive")
        class Positive {

            @Test
            @DisplayName("Should return response with status 200 with body containing account with status ACTIVE")
            void unblockAccount_should_return_status_ok_with_active_account() throws Exception {
                //given
                Account account = AccountsModuleTestData.buildDefaultAccount();
                account.setAccountState(AccountState.BLOCKED);

                txTemplate.execute(status -> {
                    em.persist(account);
                    return status;
                });

                //when
                ResultActions resultActions = mockMvc.perform(put("%s/id/%d/unblock".formatted(BASE_API, account.getId())));

                //then
                resultActions.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.accountState", is("ACTIVE")));
            }
        }

        @Nested
        @DisplayName("Negative")
        class Negative {

            @Test
            @DisplayName("Should return response with status 404 with body containing exception message")
            void unblockAccount_should_return_status_not_found() throws Exception {
                //given

                //when
                ResultActions resultActions = mockMvc.perform(put("%s/id/%d/unblock".formatted(BASE_API, 1L)));

                //then
                resultActions.andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message", is(ExceptionMessage.ACCOUNT_NOT_FOUND)));
            }

            @Test
            @DisplayName("Should return response with status 400 when account is archival")
            void unblockAccount_should_return_status_bad_request_when_account_is_archival() throws Exception {
                //given
                Account account = AccountsModuleTestData.buildDefaultAccount();
                account.setAccountState(AccountState.BLOCKED);

                txTemplate.execute(status -> {
                    em.persist(account);
                    account.setArchival(true);
                    return status;
                });

                //when
                ResultActions resultActions = mockMvc.perform(put("%s/id/%d/unblock".formatted(BASE_API, account.getId())));

                //then
                resultActions.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message", is(ExceptionMessage.ACCOUNT_ARCHIVAL)));
            }

            @ParameterizedTest
            @EnumSource(value = AccountState.class, names = {"ACTIVE", "NOT_VERIFIED"})
            @DisplayName("Should return response with status 400 when account is not BLOCKED")
            void unblockAccount_should_return_status_bad_request_when_account_is_not_BLOCKED(AccountState accountState) throws Exception {
                //given
                Account account = AccountsModuleTestData.buildDefaultAccount();
                account.setAccountState(accountState);

                txTemplate.execute(status -> {
                    em.persist(account);
                    return status;
                });

                //when
                ResultActions resultActions = mockMvc.perform(put("%s/id/%d/unblock".formatted(BASE_API, account.getId())));

                //then
                resultActions.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message", is(ExceptionMessage.ACCOUNT_NOT_BLOCKED)));
            }
        }
    }

    @Nested
    @DisplayName("PUT archive()")
    class ArchiveAccount {

        @Nested
        @DisplayName("Positive")
        class Positive {

            @ParameterizedTest
            @EnumSource(value = AccountState.class, names = {"ACTIVE", "BLOCKED", "NOT_VERIFIED"})
            @DisplayName("Should return response with status 200 with body containing archival account")
            void archiveAccount_should_return_status_ok_with_archival_account(AccountState state) throws Exception {
                //given
                Account account = AccountsModuleTestData.buildDefaultAccount();
                account.setAccountState(state);

                txTemplate.execute(status -> {
                    em.persist(account);
                    return status;
                });

                //when
                ResultActions resultActions = mockMvc.perform(put("%s/id/%d/archive".formatted(BASE_API, account.getId())));

                //then
                resultActions.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.archival", is(true)));
            }
        }

        @Nested
        @DisplayName("Negative")
        class Negative {

            @Test
            @DisplayName("Should return response with status 404 with body containing exception message")
            void archiveAccount_should_return_status_not_found() throws Exception {
                //given

                //when
                ResultActions resultActions = mockMvc.perform(put("%s/id/%d/archive".formatted(BASE_API, 1L)));

                //then
                resultActions.andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message", is(ExceptionMessage.ACCOUNT_NOT_FOUND)));
            }

            @Test
            @DisplayName("Should return response with status 400 when account is archival")
            void archiveAccount_should_return_status_bad_request_when_account_is_archival() throws Exception {
                //given
                Account account = AccountsModuleTestData.buildDefaultAccount();

                txTemplate.execute(status -> {
                    em.persist(account);
                    account.setArchival(true);
                    return status;
                });

                //when
                ResultActions resultActions = mockMvc.perform(put("%s/id/%d/archive".formatted(BASE_API, account.getId())));

                //then
                resultActions.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message", is(ExceptionMessage.ACCOUNT_ARCHIVAL)));
            }
        }
    }

    private List<Map<String, Object>> asParsedJson(String json) throws IOException {
        return objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
    }
}
