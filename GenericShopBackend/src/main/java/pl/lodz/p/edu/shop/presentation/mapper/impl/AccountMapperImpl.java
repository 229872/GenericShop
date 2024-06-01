package pl.lodz.p.edu.shop.presentation.mapper.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.lodz.p.edu.shop.dataaccess.model.embeddable.AuthLogs;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Address;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Contact;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountRole;
import pl.lodz.p.edu.shop.dataaccess.model.enumerated.AccountState;
import pl.lodz.p.edu.shop.presentation.dto.user.account.AccountOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.CreateAccountDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.RegisterDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.UpdateContactDto;
import pl.lodz.p.edu.shop.presentation.dto.user.address.AddressOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.user.log.AuthLogOutputDto;
import pl.lodz.p.edu.shop.presentation.mapper.api.AccountMapper;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor

@Slf4j
@Component
class AccountMapperImpl implements AccountMapper {

    private final PasswordEncoder passwordEncoder;

    @Override
    public Account mapToAccount(CreateAccountDto createDto) {
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
            .password(passwordEncoder.encode(createDto.password()))
            .email(createDto.email())
            .locale(createDto.locale())
            .accountState(AccountState.valueOf(createDto.accountState().toUpperCase()))
            .accountRoles(new HashSet<>(Set.of(AccountRole.valueOf(createDto.role().toUpperCase()))))
            .contact(contact)
            .build();
    }

    @Override
    public Account mapToAccount(RegisterDto registerDto) {
        var addressDto = registerDto.address();

        Address address = Address.builder()
            .postalCode(addressDto.postalCode())
            .country(addressDto.country())
            .city(addressDto.city())
            .street(addressDto.street())
            .houseNumber(addressDto.houseNumber())
            .build();

        Contact contact = Contact.builder()
            .firstName(registerDto.firstName())
            .lastName(registerDto.lastName())
            .address(address)
            .build();

        return Account.builder()
            .login(registerDto.login())
            .password(passwordEncoder.encode(registerDto.password()))
            .email(registerDto.email())
            .locale(registerDto.locale())
            .accountState(AccountState.NOT_VERIFIED)
            .accountRoles(Set.of(AccountRole.CLIENT))
            .contact(contact)
            .build();
    }

    @Override
    public Contact mapToContact(UpdateContactDto updateDto) {
        Address address = Address.builder()
            .country(updateDto.country())
            .city(updateDto.city())
            .postalCode(updateDto.postalCode())
            .street(updateDto.street())
            .houseNumber(updateDto.houseNumber())
            .build();

        return Contact.builder()
            .firstName(updateDto.firstName())
            .lastName(updateDto.lastName())
            .address(address)
            .build();
    }

    @Override
    public AccountOutputDto mapToAccountOutputDto(Account account) {
        Contact contact = account.getContact();
        Address address = contact.getAddress();
        AuthLogs authLogs = account.getAuthLogs();

        String combinedVersion = String.valueOf(contact.getVersion() + address.getVersion());

        AddressOutputDto addressDto = AddressOutputDto.builder()
            .postalCode(address.getPostalCode())
            .country(address.getCountry())
            .city(address.getCity())
            .street(address.getStreet())
            .houseNumber(address.getHouseNumber())
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
            .version(combinedVersion)
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
