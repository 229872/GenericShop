package pl.lodz.p.edu.presentation.mapper;

import org.springframework.stereotype.Component;
import pl.lodz.p.edu.dataaccess.model.Account;
import pl.lodz.p.edu.dataaccess.model.Address;
import pl.lodz.p.edu.dataaccess.model.Person;
import pl.lodz.p.edu.dataaccess.model.sub.AccountRole;
import pl.lodz.p.edu.dataaccess.model.sub.AccountState;
import pl.lodz.p.edu.presentation.dto.user.account.AccountCreateDto;
import pl.lodz.p.edu.presentation.dto.user.account.AccountOutputDto;
import pl.lodz.p.edu.presentation.dto.user.address.AddressOutputDto;

import java.util.HashSet;
import java.util.Set;

@Component
public class AccountMapper {

    public Account mapToAccount(AccountCreateDto createDto) {
        var addressDto = createDto.address();

        Address address = Address.builder()
            .postalCode(addressDto.postalCode())
            .country(addressDto.country())
            .city(addressDto.city())
            .street(addressDto.street())
            .houseNumber(addressDto.houseNumber())
            .build();

        Person person = Person.builder()
            .firstName(createDto.firstName())
            .lastName(createDto.lastName())
            .address(address)
            .build();

        return Account.builder()
            .login(createDto.login())
            .password(createDto.password())
            .email(createDto.email())
            .locale(createDto.locale())
            .accountState(AccountState.valueOf(createDto.accountState().toUpperCase()))
            .accountRoles(new HashSet<>(Set.of(AccountRole.valueOf(createDto.role().toUpperCase()))))
            .person(person)
            .build();
    }

    public AccountOutputDto mapToAccountOutputDto(Account account) {
        Person person = account.getPerson();

        AddressOutputDto addressDto = AddressOutputDto.builder()
            .postalCode(person.getPostalCode())
            .country(person.getCountry())
            .city(person.getCity())
            .street(person.getStreet())
            .houseNumber(person.getHouseNumber())
            .build();

        return AccountOutputDto.builder()
            .id(account.getId())
            .archival(account.isArchival())
            .login(account.getLogin())
            .email(account.getEmail())
            .locale(account.getLocale())
            .state(account.getAccountState().name())
            .roles(account.getAccountRoles().stream().map(AccountRole::name).toList())
            .firstName(person.getFirstName())
            .lastName(person.getLastName())
            .address(addressDto)
            .build();
    }
}
