package pl.lodz.p.edu.shop.presentation.controller;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.edu.shop.presentation.adapter.api.AccountAccessServiceOperations;
import pl.lodz.p.edu.shop.presentation.dto.user.account.AccountOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.AccountRegisterDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.ChangeLanguageDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.ChangePasswordDto;
import pl.lodz.p.edu.shop.config.security.role.RoleName;

import java.net.URI;

import static pl.lodz.p.edu.shop.presentation.controller.ApiRoot.API_ROOT;
import static pl.lodz.p.edu.shop.util.SecurityUtil.getLoginFromSecurityContext;

@RequiredArgsConstructor

@RestController
@RequestMapping( API_ROOT + "/account/self")
@DenyAll
public class SelfAccountController {

    private final AccountAccessServiceOperations ownAccountService;

    @GetMapping
    @RolesAllowed({RoleName.CLIENT, RoleName.EMPLOYEE, RoleName.ADMIN})
    ResponseEntity<AccountOutputDto> getOwnAccountInformation() {
        String login = getLoginFromSecurityContext();
        AccountOutputDto result = ownAccountService.findByLogin(login);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/change-locale")
    @RolesAllowed({RoleName.CLIENT, RoleName.ADMIN, RoleName.EMPLOYEE})
    ResponseEntity<AccountOutputDto> changeOwnLocale(@RequestBody @Valid ChangeLanguageDto locale) {
        String login = getLoginFromSecurityContext();
        AccountOutputDto result = ownAccountService.updateOwnLocale(login, locale);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/change-password")
    @RolesAllowed({RoleName.CLIENT, RoleName.ADMIN, RoleName.EMPLOYEE})
    ResponseEntity<AccountOutputDto> changeOwnPassword(@RequestBody @Valid ChangePasswordDto passwords) {
        String login = getLoginFromSecurityContext();
        AccountOutputDto result = ownAccountService.changePassword(login, passwords);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/register")
    @RolesAllowed({RoleName.GUEST})
    ResponseEntity<AccountOutputDto> register(@RequestBody @Valid AccountRegisterDto registerDto) {
        AccountOutputDto responseBody = ownAccountService.register(registerDto);

        return ResponseEntity.created(URI.create("/id/%d".formatted(responseBody.id()))).body(responseBody);
    }
}
