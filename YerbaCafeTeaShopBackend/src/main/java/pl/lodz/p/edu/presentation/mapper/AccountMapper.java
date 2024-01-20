package pl.lodz.p.edu.presentation.mapper;

import org.springframework.stereotype.Component;
import pl.lodz.p.edu.dataaccess.model.entity.Account;
import pl.lodz.p.edu.dataaccess.model.entity.Address;
import pl.lodz.p.edu.dataaccess.model.embeddable.AuthLogs;
import pl.lodz.p.edu.dataaccess.model.entity.Contact;
import pl.lodz.p.edu.dataaccess.model.enumerated.AccountRole;
import pl.lodz.p.edu.dataaccess.model.enumerated.AccountState;
import pl.lodz.p.edu.presentation.dto.user.account.AccountCreateDto;
import pl.lodz.p.edu.presentation.dto.user.account.AccountOutputDto;
import pl.lodz.p.edu.presentation.dto.user.address.AddressOutputDto;
import pl.lodz.p.edu.presentation.dto.user.log.AuthLogOutputDto;

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

        Contact contact = Contact.builder()
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
            .contact(contact)
            .build();
    }

    public AccountOutputDto mapToAccountOutputDto(Account account) {
        Contact contact = account.getContact();
        AuthLogs authLogs = account.getAuthLogs();

        AddressOutputDto addressDto = AddressOutputDto.builder()
            .postalCode(contact.getPostalCode())
            .country(contact.getCountry())
            .city(contact.getCity())
            .street(contact.getStreet())
            .houseNumber(contact.getHouseNumber())
            .build();

        AuthLogOutputDto logs = AuthLogOutputDto.builder()
            .lastSuccessfulAuthIpAddr(authLogs.getLastSuccessfulAuthIpAddr())
            .lastUnsuccessfulAuthIpAddr(authLogs.getLastUnsuccessfulAuthIpAddr())
            .lastSuccessfulAuthTime(authLogs.getLastSuccessfulAuthTime())
            .lastUnsuccessfulAuthTime(authLogs.getLastUnsuccessfulAuthTime())
            .unsuccessfulAuthCounter(authLogs.getUnsuccessfulAuthCounter())
            .blockadeEndTime(authLogs.getBlockadeEndTime())
            .build();

        return AccountOutputDto.builder()
            .id(account.getId())
            .archival(account.isArchival())
            .login(account.getLogin())
            .email(account.getEmail())
            .locale(account.getLocale())
            .state(account.getAccountState().name())
            .roles(account.getAccountRoles().stream().map(AccountRole::name).toList())
            .firstName(contact.getFirstName())
            .lastName(contact.getLastName())
            .address(addressDto)
            .authLogs(logs)
            .build();
    }
}
