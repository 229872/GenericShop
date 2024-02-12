package pl.lodz.p.edu;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.lodz.p.edu.dataaccess.model.entity.Account;
import pl.lodz.p.edu.dataaccess.model.entity.Address;
import pl.lodz.p.edu.dataaccess.model.embeddable.AuthLogs;
import pl.lodz.p.edu.dataaccess.model.entity.Contact;
import pl.lodz.p.edu.dataaccess.model.enumerated.AccountRole;
import pl.lodz.p.edu.dataaccess.model.enumerated.AccountState;
import pl.lodz.p.edu.presentation.dto.user.account.AccountCreateDto;
import pl.lodz.p.edu.presentation.dto.user.address.AddressCreateDto;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestData {
    static int counter = 1;

    public static final String defaultPostalCode = "11-111";
    public static final String defaultCountry = "Country";
    public static final String defaultCity = "City";
    public static final String defaultStreet = "Street";
    public static final Integer defaultHouseNumber = 30;

    public static final String defaultLogin = "Login";
    public static final String defaultEmail = "example@example.com";
    public static final String defaultEncryptedPassword = "$2a$12$peey4Pc/Dn7PJsvBWOTLvOsNuOypGlKAYd7UG3E1kBmZkl/x65boG";
    public static final String defaultLocale = "en";
    public static final AccountState defaultAccountState = AccountState.ACTIVE;
    public static final AccountRole defaultRole = AccountRole.CLIENT;
    public static final Set<AccountRole> defaultAccountRoles = new HashSet<>(Set.of(defaultRole));
    public static final AuthLogs defaultAuthLogs = AuthLogs.builder().unsuccessfulAuthCounter(0).build();
    public static final String defaultCreatedBy = "testUser";
    public static final String defaultPassword = "Student123!";
    public static final Boolean defaultArchival = false;

    public static final String defaultFirstName = "FirstName";
    public static final String defaultLastName = "LastName";

    public static Address.AddressBuilder<?,?> getDefaultAddressBuilder() {
        return Address.builder()
            .postalCode(defaultPostalCode)
            .country(defaultCountry)
            .city(defaultCity)
            .street(defaultStreet)
            .houseNumber(defaultHouseNumber)
            .createdBy(defaultCreatedBy)
            .isArchival(defaultArchival);
    }

    public static Contact.ContactBuilder<?,?> getDefaultContactBuilder() {
        return Contact.builder()
            .firstName(defaultFirstName)
            .lastName(defaultLastName)
            .address(buildDefaultAddress())
            .createdBy(defaultCreatedBy)
            .isArchival(defaultArchival);
    }

    public static Account.AccountBuilder<?,?> getDefaultAccountBuilder() {
        return Account.builder()
            .login(defaultLogin)
            .email(defaultEmail)
            .password(defaultEncryptedPassword)
            .locale(defaultLocale)
            .contact(buildDefaultContact())
            .accountState(defaultAccountState)
            .accountRoles(defaultAccountRoles)
            .createdBy(defaultCreatedBy)
            .authLogs(defaultAuthLogs)
            .isArchival(defaultArchival);
    }


    public static Address buildDefaultAddress() {
        return getDefaultAddressBuilder()
            .build();
    }

    public static Contact buildDefaultContact() {
        return getDefaultContactBuilder()
            .build();
    }

    public static Account buildDefaultAccount() {
        String uniqueLogin = defaultLogin + counter;
        String uniqueEmail = defaultEmail + counter;
        counter++;
        return getDefaultAccountBuilder()
            .login(uniqueLogin)
            .email(uniqueEmail)
            .build();
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
