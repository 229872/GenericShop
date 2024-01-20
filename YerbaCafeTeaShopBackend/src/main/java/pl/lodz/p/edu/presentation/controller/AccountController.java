package pl.lodz.p.edu.presentation.controller;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.edu.dataaccess.model.entity.Account;
import pl.lodz.p.edu.logic.service.api.AccountService;
import pl.lodz.p.edu.presentation.dto.user.account.AccountCreateDto;
import pl.lodz.p.edu.presentation.dto.user.account.AccountOutputDto;
import pl.lodz.p.edu.presentation.dto.user.account.ChangeLanguageDto;
import pl.lodz.p.edu.presentation.dto.user.account.ChangePasswordDto;
import pl.lodz.p.edu.presentation.mapper.AccountMapper;

import java.net.URI;
import java.util.List;
import java.util.Locale;

import static pl.lodz.p.edu.config.security.RoleName.*;
import static pl.lodz.p.edu.util.SecurityUtil.getLoginFromSecurityContext;

@RequiredArgsConstructor

@RestController
@RequestMapping("/api/account")
@DenyAll
public class AccountController {

    private final AccountService accountService;

    private final AccountMapper accountMapper;

    @GetMapping
    @RolesAllowed(ADMIN)
    ResponseEntity<List<AccountOutputDto>> getAll() {
        List<AccountOutputDto> result = accountService.findAll().stream()
            .map(accountMapper::mapToAccountOutputDto)
            .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/id/{id}")
    @RolesAllowed(ADMIN)
    ResponseEntity<AccountOutputDto> getById(@PathVariable Long id) {
        Account account = accountService.findById(id);
        AccountOutputDto result = accountMapper.mapToAccountOutputDto(account);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/self")
    @RolesAllowed({CLIENT, EMPLOYEE, ADMIN})
    ResponseEntity<AccountOutputDto> getOwnAccountInformation() {
        String login = getLoginFromSecurityContext();
        Account account = accountService.findByLogin(login);
        AccountOutputDto result = accountMapper.mapToAccountOutputDto(account);

        return ResponseEntity.ok(result);
    }


    @PostMapping
    @RolesAllowed(ADMIN)
    ResponseEntity<AccountOutputDto> createAccount(@RequestBody @Valid AccountCreateDto createDto) {
        Account account = accountMapper.mapToAccount(createDto);
        Account createdAccount = accountService.create(account);
        AccountOutputDto result = accountMapper.mapToAccountOutputDto(createdAccount);

        return ResponseEntity.created(URI.create("/id/%d".formatted(result.id()))).body(result);
    }

    @PutMapping("/id/{id}/block")
    @RolesAllowed(ADMIN)
    ResponseEntity<AccountOutputDto> blockAccount(@PathVariable Long id) {
        Account account = accountService.block(id);
        AccountOutputDto result = accountMapper.mapToAccountOutputDto(account);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/id/{id}/unblock")
    @RolesAllowed(ADMIN)
    ResponseEntity<AccountOutputDto> unblockAccount(@PathVariable Long id) {
        Account account = accountService.unblock(id);
        AccountOutputDto result = accountMapper.mapToAccountOutputDto(account);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/id/{id}/archive")
    @RolesAllowed(ADMIN)
    ResponseEntity<AccountOutputDto> archiveAccount(@PathVariable Long id) {
        Account account = accountService.archive(id);
        AccountOutputDto result = accountMapper.mapToAccountOutputDto(account);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/self/change-locale")
    @RolesAllowed({CLIENT, ADMIN, EMPLOYEE})
    ResponseEntity<AccountOutputDto> changeOwnLocale(@RequestBody @Valid ChangeLanguageDto locale) {
        Locale language = new Locale(locale.locale());
        String login = getLoginFromSecurityContext();
        Account account = accountService.updateOwnLocale(login, language);
        AccountOutputDto result = accountMapper.mapToAccountOutputDto(account);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/self/change-password")
    @RolesAllowed({CLIENT, ADMIN, EMPLOYEE})
    ResponseEntity<AccountOutputDto> changeOwnPassword(@RequestBody @Valid ChangePasswordDto passwords) {
        String login = getLoginFromSecurityContext();
        Account account = accountService.changePassword(login, passwords.currentPassword(), passwords.newPassword());
        AccountOutputDto result = accountMapper.mapToAccountOutputDto(account);

        return ResponseEntity.ok(result);
    }


}
