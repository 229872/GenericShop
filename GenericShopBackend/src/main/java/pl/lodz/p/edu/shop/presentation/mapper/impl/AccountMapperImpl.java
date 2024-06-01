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
import pl.lodz.p.edu.shop.presentation.dto.user.address.InputAddressDto;
import pl.lodz.p.edu.shop.presentation.dto.user.log.AuthLogOutputDto;
import pl.lodz.p.edu.shop.presentation.mapper.api.AccountMapper;
import pl.lodz.p.edu.shop.util.SecurityUtil;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor

@Slf4j
@Component
class AccountMapperImpl implements AccountMapper {

    private final PasswordEncoder passwordEncoder;

    @Override
    public Account mapToAccount(CreateAccountDto createDto) {
        Address address = mapToAddress(createDto.address());
        Contact contact = mapToContact(createDto.firstName(), createDto.lastName(), address);

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
        Address address = mapToAddress(registerDto.address());
        Contact contact = mapToContact(registerDto.firstName(), registerDto.lastName(), address);

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
        Address address = mapToAddress(updateDto.address());

        return mapToContact(updateDto.firstName(), updateDto.lastName(), address);
    }

    @Override
    public AccountOutputDto mapToAccountOutputDtoWithoutVersion(Account account) {
        Contact contact = account.getContact();
        Address address = contact.getAddress();

        AddressOutputDto addressDto = mapToAddressOutputDto(address);
        AuthLogOutputDto logs = mapToAuthLogOutputDto(account.getAuthLogs());

        return mapToAccountOutputDto(account, contact, addressDto, logs)
            .build();
    }

    @Override
    public AccountOutputDto mapToAccountOutputDtoWithVersion(Account account) {
        Contact contact = account.getContact();
        Address address = contact.getAddress();

        String combinedVersion = SecurityUtil.signVersion(contact.getVersion() + contact.getAddress().getVersion());
        AddressOutputDto addressDto = mapToAddressOutputDto(address);
        AuthLogOutputDto logs = mapToAuthLogOutputDto(account.getAuthLogs());

        return mapToAccountOutputDto(account, contact, addressDto, logs)
            .version(combinedVersion)
            .build();
    }

    private AccountOutputDto.AccountOutputDtoBuilder mapToAccountOutputDto(Account account, Contact contact,
                                                                           AddressOutputDto addressDto, AuthLogOutputDto logs) {
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
            .authLogs(logs);
    }

    private AddressOutputDto mapToAddressOutputDto(Address address) {
        return AddressOutputDto.builder()
            .postalCode(address.getPostalCode())
            .country(address.getCountry())
            .city(address.getCity())
            .street(address.getStreet())
            .houseNumber(address.getHouseNumber())
            .build();
    }

    private AuthLogOutputDto mapToAuthLogOutputDto(AuthLogs authLogs) {
        return AuthLogOutputDto.builder()
            .lastSuccessfulAuthIpAddr(authLogs.getLastSuccessfulAuthIpAddr())
            .lastUnsuccessfulAuthIpAddr(authLogs.getLastUnsuccessfulAuthIpAddr())
            .lastSuccessfulAuthTime(authLogs.getLastSuccessfulAuthTime())
            .lastUnsuccessfulAuthTime(authLogs.getLastUnsuccessfulAuthTime())
            .unsuccessfulAuthCounter(authLogs.getUnsuccessfulAuthCounter())
            .blockadeEndTime(authLogs.getBlockadeEndTime())
            .build();
    }

    private Address mapToAddress(InputAddressDto addressDto) {
        return Address.builder()
            .postalCode(addressDto.postalCode())
            .country(addressDto.country())
            .city(addressDto.city())
            .street(addressDto.street())
            .houseNumber(addressDto.houseNumber())
            .build();
    }

    private Contact mapToContact(String firstName, String lastName, Address address) {
        return Contact.builder()
            .firstName(firstName)
            .lastName(lastName)
            .address(address)
            .build();
    }
}
