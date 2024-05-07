package pl.lodz.p.edu.shop.presentation.controller;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.edu.shop.logic.model.JwtTokens;
import pl.lodz.p.edu.shop.logic.service.api.AuthenticationService;
import pl.lodz.p.edu.shop.presentation.dto.authentication.Credentials;
import pl.lodz.p.edu.shop.presentation.dto.authentication.Tokens;

import static pl.lodz.p.edu.shop.config.security.role.RoleName.*;
import static pl.lodz.p.edu.shop.presentation.controller.ApiRoot.API_ROOT;
import static pl.lodz.p.edu.shop.util.SecurityUtil.getLoginFromSecurityContext;

@RequiredArgsConstructor

@RestController
@RequestMapping(API_ROOT + "/auth")
@DenyAll
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping
    @RolesAllowed(GUEST)
    ResponseEntity<Tokens> authenticate(@RequestBody @Valid Credentials credentials) {
        JwtTokens jwtTokens = authenticationService.authenticate(credentials.login(), credentials.password());
        Tokens responseBody = new Tokens(jwtTokens.token(), jwtTokens.refreshToken());

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/extend/{refreshToken}")
    @RolesAllowed({CLIENT, ADMIN, EMPLOYEE})
    ResponseEntity<Tokens> extendSession(@PathVariable String refreshToken) {
        String login = getLoginFromSecurityContext();
        JwtTokens jwtTokens = authenticationService.extendSession(login, refreshToken);
        Tokens responseBody = new Tokens(jwtTokens.token(), jwtTokens.refreshToken());

        return ResponseEntity.ok(responseBody);
    }
}
