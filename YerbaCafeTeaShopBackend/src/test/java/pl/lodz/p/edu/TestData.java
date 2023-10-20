package pl.lodz.p.edu;

import pl.lodz.p.edu.dataaccess.model.Account;
import pl.lodz.p.edu.dataaccess.model.Address;
import pl.lodz.p.edu.dataaccess.model.Person;
import pl.lodz.p.edu.dataaccess.model.sub.AccountRole;
import pl.lodz.p.edu.dataaccess.model.sub.AccountState;
import pl.lodz.p.edu.presentation.dto.user.account.AccountCreateDto;
import pl.lodz.p.edu.presentation.dto.user.address.AddressCreateDto;

import java.util.HashSet;
import java.util.Set;

public class TestData {
    static int counter = 1;

    static final String defaultPostalCode = "11-111";
    static final String defaultCountry = "Country";
    static final String defaultCity = "City";
    static final String defaultStreet = "Street";
    static final Integer defaultHouseNumber = 30;

    static final String defaultLogin = "Login";
    static final String defaultEmail = "example@example.com";
    static final String defaultEncryptedPassword = "$2a$12$JhPWlsiSf3S9I32figUWX.SG9toKfm6biTgGCC1m6vqqlxtU1ripq!";
    static final String defaultLocale = "en";
    static final AccountState defaultAccountState = AccountState.ACTIVE;
    static final AccountRole defaultRole = AccountRole.CLIENT;
    static final Set<AccountRole> defaultAccountRoles = new HashSet<>(Set.of(defaultRole));
    static final String defaultCreatedBy = "testUser";
    static final String defaultPassword = "Student123!";

    static final String defaultFirstName = "FirstName";
    static final String defaultLastName = "LastName";

    public static Address buildFullAddress(String postalCode, String country, String city, String street,
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

    public static Person buildFullPerson(String firstName, String lastName, Address address, String createdBy) {
        return Person.builder()
            .firstName(firstName)
            .lastName(lastName)
            .address(address)
            .createdBy(createdBy)
            .build();
    }

    public static Account buildFullAccount(String login, String email, String password, String locale, Person person,
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


    public static Address buildDefaultAddress() {
        return buildFullAddress(defaultPostalCode, defaultCountry, defaultCity, defaultStreet, defaultHouseNumber,
            defaultCreatedBy);
    }

    public static Person buildDefaultPerson() {
        return buildFullPerson(defaultFirstName, defaultLastName, buildDefaultAddress(), defaultCreatedBy);
    }

    public static Account buildDefaultAccount() {
        String uniqueLogin = defaultLogin + counter;
        String uniqueEmail = defaultEmail + counter;
        counter++;
        return buildFullAccount(uniqueLogin, uniqueEmail, defaultEncryptedPassword, defaultLocale, buildDefaultPerson(),
            defaultAccountState, defaultAccountRoles, defaultCreatedBy);
    }

    public static AddressCreateDto buildDefaultAddressCreateDto() {
        return getDefaultAddressCreateDtoBuilder().build();
    }

    public static AccountCreateDto buildDefaultAccountCreateDto() {
        return getDefaultAccountCreateDtoBuilder().build();
    }

    public static AddressCreateDto.AddressCreateDtoBuilder getDefaultAddressCreateDtoBuilder() {
        return AddressCreateDto.builder()
            .postalCode(defaultPostalCode)
            .country(defaultCountry)
            .city(defaultCity)
            .street(defaultStreet)
            .houseNumber(defaultHouseNumber);
    }

    public static AccountCreateDto.AccountCreateDtoBuilder getDefaultAccountCreateDtoBuilder() {
        return AccountCreateDto.builder()
            .login(defaultLogin)
            .email(defaultEmail)
            .password(defaultPassword)
            .locale(defaultLocale)
            .firstName(defaultFirstName)
            .lastName(defaultLastName)
            .address(buildDefaultAddressCreateDto())
            .accountState(defaultAccountState.name())
            .role(defaultRole.name());
    }

    public static void resetCounter() {
        counter = 1;
    }
}
