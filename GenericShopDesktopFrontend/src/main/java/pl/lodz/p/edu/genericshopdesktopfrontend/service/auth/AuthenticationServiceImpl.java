package pl.lodz.p.edu.genericshopdesktopfrontend.service.auth;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.Role;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.Tokens;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

enum AuthenticationServiceImpl implements AuthenticationService {

    INSTANCE;

    private Role activeRole = Role.GUEST;

    private Set<Role> roles = new HashSet<>();

    private String authToken;

    private Optional<String> authToken() {
        return Optional.ofNullable(authToken);
    }

    private String refreshToken;

    private Optional<String> refreshToken() {
        return Optional.ofNullable(refreshToken);
    }


    @Override
    public void authenticate(Tokens tokens) throws ApplicationException {
        try {
            authToken = tokens.authToken();
            refreshToken = tokens.refreshToken();

            JWT parsedToken = JWTParser.parse(tokens.authToken());
            JWTClaimsSet claims = parsedToken.getJWTClaimsSet();

            String role = claims.getClaimAsString("activeRole");
            this.activeRole = Role.valueOf(role);

        } catch (ParseException | IllegalArgumentException e) {
            throw new ApplicationException("Could not authenticate", e);
        }
    }
}
