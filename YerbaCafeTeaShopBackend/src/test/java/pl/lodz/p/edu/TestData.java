package pl.lodz.p.edu;

import pl.lodz.p.edu.dataaccess.model.Account;
import pl.lodz.p.edu.dataaccess.model.Address;
import pl.lodz.p.edu.dataaccess.model.Person;
import pl.lodz.p.edu.dataaccess.model.sub.AccountRole;
import pl.lodz.p.edu.dataaccess.model.sub.AccountState;

import java.util.HashSet;
import java.util.Set;

public class TestData {
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
        return buildFullAccount(uniqueLogin, uniqueEmail, defaultPassword, defaultLocale, buildDefaultPerson(),
            defaultAccountState, defaultAccountRoles, defaultCreatedBy);
    }

    public static void resetCounter() {
        counter = 1;
    }
}
