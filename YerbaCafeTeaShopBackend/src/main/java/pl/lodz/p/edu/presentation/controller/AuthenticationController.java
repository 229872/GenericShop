package pl.lodz.p.edu.presentation.controller;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.edu.logic.model.JwtTokens;
import pl.lodz.p.edu.logic.service.api.AuthenticationService;
import pl.lodz.p.edu.presentation.dto.authentication.Credentials;
import pl.lodz.p.edu.presentation.dto.authentication.Tokens;

import static pl.lodz.p.edu.config.RoleName.GUEST;

@RequiredArgsConstructor

@RestController
@RequestMapping("/api/auth")
@DenyAll
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping
    @RolesAllowed(GUEST)
    ResponseEntity<Tokens> authenticate(@RequestBody @Valid Credentials credentials) {
        JwtTokens jwtTokens = authenticationService.authenticate(credentials.login(), credentials.password());
        Tokens tokens = new Tokens(jwtTokens.token(), jwtTokens.refreshToken());

        return ResponseEntity.ok(tokens);
    }
}
