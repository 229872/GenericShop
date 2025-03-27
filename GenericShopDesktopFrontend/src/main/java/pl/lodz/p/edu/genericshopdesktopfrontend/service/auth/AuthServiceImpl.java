package pl.lodz.p.edu.genericshopdesktopfrontend.service.auth;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.Role;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.Tokens;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

enum AuthServiceImpl implements AuthService {

    INSTANCE;


    private Role activeRole = Role.GUEST;
    private Set<Role> roles = new HashSet<>();
    private String authToken = null;
    private String refreshToken = null;
    private String login = null;


    @Override
    public void authenticate(Tokens tokens) throws ApplicationException {
        try {
            authToken = tokens.authToken();
            refreshToken = tokens.refreshToken();

            JWT parsedToken = JWTParser.parse(tokens.authToken());
            JWTClaimsSet claims = parsedToken.getJWTClaimsSet();

            String[] textRoles = claims.getStringArrayClaim("accountRoles");

            login = claims.getSubject();

            roles = Arrays.stream(textRoles)
                .map(Role::valueOf)
                .collect(Collectors.toSet());

            activeRole = roles.stream()
                .findFirst()
                .orElse(Role.GUEST);

        } catch (ParseException | IllegalArgumentException e) {
            throw new ApplicationException("Could not authenticate", e);
        }
    }


    @Override
    public void logout() {
        authToken = null;
        refreshToken = null;
        login = null;
        roles.clear();
        activeRole = Role.GUEST;
    }


    @Override
    public Set<Role> getAccountRoles() {
        return Set.copyOf(roles);
    }


    @Override
    public Role getActiveRole() {
        return activeRole;
    }


    @Override
    public Role setActiveRole(Role newActiveRole) {
        if (nonNull(newActiveRole)) {
            this.activeRole = newActiveRole;
        }
        return this.activeRole;
    }


    @Override
    public Optional<String> getLogin() {
        return Optional.ofNullable(login);
    }


    @Override
    public Optional<String> getAuthToken() {
        return Optional.ofNullable(authToken);
    }


    @Override
    public Optional<String> getRefreshToken() {
        return Optional.ofNullable(refreshToken);
    }
}
