package pl.lodz.p.edu.presentation.controller;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.edu.presentation.adapter.api.OwnAccountServiceOperations;
import pl.lodz.p.edu.presentation.dto.user.account.AccountOutputDto;
import pl.lodz.p.edu.presentation.dto.user.account.ChangeLanguageDto;
import pl.lodz.p.edu.presentation.dto.user.account.ChangePasswordDto;

import static pl.lodz.p.edu.config.security.role.RoleName.*;
import static pl.lodz.p.edu.util.SecurityUtil.getLoginFromSecurityContext;

@RequiredArgsConstructor

@RestController
@RequestMapping("/api/account/self")
@DenyAll
public class SelfAccountController {

    private final OwnAccountServiceOperations ownAccountService;

    @GetMapping
    @RolesAllowed({CLIENT, EMPLOYEE, ADMIN})
    ResponseEntity<AccountOutputDto> getOwnAccountInformation() {
        String login = getLoginFromSecurityContext();
        AccountOutputDto result = ownAccountService.findByLogin(login);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/change-locale")
    @RolesAllowed({CLIENT, ADMIN, EMPLOYEE})
    ResponseEntity<AccountOutputDto> changeOwnLocale(@RequestBody @Valid ChangeLanguageDto locale) {
        String login = getLoginFromSecurityContext();
        AccountOutputDto result = ownAccountService.updateOwnLocale(login, locale);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/change-password")
    @RolesAllowed({CLIENT, ADMIN, EMPLOYEE})
    ResponseEntity<AccountOutputDto> changeOwnPassword(@RequestBody @Valid ChangePasswordDto passwords) {
        String login = getLoginFromSecurityContext();
        AccountOutputDto result = ownAccountService.changePassword(login, passwords);

        return ResponseEntity.ok(result);
    }
}
