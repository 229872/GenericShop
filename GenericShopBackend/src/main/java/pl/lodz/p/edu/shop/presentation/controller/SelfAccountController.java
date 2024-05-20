package pl.lodz.p.edu.shop.presentation.controller;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.edu.shop.presentation.adapter.api.AccountAccessServiceOperations;
import pl.lodz.p.edu.shop.presentation.dto.user.account.*;
import pl.lodz.p.edu.shop.config.security.role.RoleName;

import java.net.URI;

import static pl.lodz.p.edu.shop.presentation.controller.ApiRoot.API_ROOT;
import static pl.lodz.p.edu.shop.util.SecurityUtil.getLoginFromSecurityContext;

@RequiredArgsConstructor

@RestController
@RequestMapping( API_ROOT + "/account/self")
@Validated
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

    @PutMapping("/register/confirm")
    @RolesAllowed({RoleName.GUEST})
    ResponseEntity<Void> confirmRegistration(@RequestParam("token") String token) {
        ownAccountService.confirmRegistration(token);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/forgot-password")
    @RolesAllowed({RoleName.GUEST})
    ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordDto forgotPasswordDto) {
        ownAccountService.forgotPassword(forgotPasswordDto);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/reset-password/validate")
    @RolesAllowed({RoleName.GUEST})
    ResponseEntity<Void> validateResetPasswordToken(@NotBlank @RequestParam("token") String token) {
        ownAccountService.validateResetPasswordToken(token);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/reset-password")
    @RolesAllowed({RoleName.GUEST})
    ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto) {
        ownAccountService.resetPassword(resetPasswordDto);

        return ResponseEntity.ok().build();
    }
}
