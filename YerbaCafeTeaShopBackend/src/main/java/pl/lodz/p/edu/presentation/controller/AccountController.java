package pl.lodz.p.edu.presentation.controller;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.edu.presentation.adapter.api.AccountServiceOperations;
import pl.lodz.p.edu.presentation.adapter.api.OwnAccountServiceOperations;
import pl.lodz.p.edu.presentation.dto.user.account.AccountCreateDto;
import pl.lodz.p.edu.presentation.dto.user.account.AccountOutputDto;
import pl.lodz.p.edu.presentation.dto.user.account.ChangeLanguageDto;
import pl.lodz.p.edu.presentation.dto.user.account.ChangePasswordDto;

import java.net.URI;
import java.util.List;

import static pl.lodz.p.edu.config.security.role.RoleName.*;
import static pl.lodz.p.edu.util.SecurityUtil.getLoginFromSecurityContext;

@RequiredArgsConstructor

@RestController
@RequestMapping("/api/account")
@DenyAll
public class AccountController {

    private final AccountServiceOperations accountService;
    private final OwnAccountServiceOperations ownAccountService;

    @GetMapping
    @RolesAllowed(ADMIN)
    ResponseEntity<List<AccountOutputDto>> getAll() {
        List<AccountOutputDto> result = accountService.findAll();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/id/{id}")
    @RolesAllowed(ADMIN)
    ResponseEntity<AccountOutputDto> getById(@PathVariable Long id) {
        AccountOutputDto result = accountService.findById(id);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/self")
    @RolesAllowed({CLIENT, EMPLOYEE, ADMIN})
    ResponseEntity<AccountOutputDto> getOwnAccountInformation() {
        String login = getLoginFromSecurityContext();
        AccountOutputDto result = ownAccountService.findByLogin(login);

        return ResponseEntity.ok(result);
    }


    @PostMapping
    @RolesAllowed(ADMIN)
    ResponseEntity<AccountOutputDto> createAccount(@RequestBody @Valid AccountCreateDto createDto) {
        AccountOutputDto result = accountService.create(createDto);

        return ResponseEntity.created(URI.create("/id/%d".formatted(result.id()))).body(result);
    }

    @PutMapping("/id/{id}/block")
    @RolesAllowed(ADMIN)
    ResponseEntity<AccountOutputDto> blockAccount(@PathVariable Long id) {
        AccountOutputDto result = accountService.block(id);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/id/{id}/unblock")
    @RolesAllowed(ADMIN)
    ResponseEntity<AccountOutputDto> unblockAccount(@PathVariable Long id) {
        AccountOutputDto result = accountService.unblock(id);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/id/{id}/archive")
    @RolesAllowed(ADMIN)
    ResponseEntity<AccountOutputDto> archiveAccount(@PathVariable Long id) {
        AccountOutputDto result = accountService.archive(id);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/self/change-locale")
    @RolesAllowed({CLIENT, ADMIN, EMPLOYEE})
    ResponseEntity<AccountOutputDto> changeOwnLocale(@RequestBody @Valid ChangeLanguageDto locale) {
        String login = getLoginFromSecurityContext();
        AccountOutputDto result = ownAccountService.updateOwnLocale(login, locale);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/self/change-password")
    @RolesAllowed({CLIENT, ADMIN, EMPLOYEE})
    ResponseEntity<AccountOutputDto> changeOwnPassword(@RequestBody @Valid ChangePasswordDto passwords) {
        String login = getLoginFromSecurityContext();
        AccountOutputDto result = ownAccountService.changePassword(login, passwords);

        return ResponseEntity.ok(result);
    }


}
