package pl.lodz.p.edu.genericshopdesktopfrontend.service.auth;

import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.Role;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.Tokens;

import java.util.Optional;
import java.util.Set;

public sealed interface AuthService permits AuthServiceImpl {

    static AuthService getInstance() {
        return AuthServiceImpl.INSTANCE;
    }

    void authenticate(Tokens tokens) throws ApplicationException;

    void logout();

    Set<Role> getAccountRoles();

    Role getActiveRole();

    Role setActiveRole(Role newActiveRole);

    Optional<String> getLogin();

    Optional<String> getAuthToken();

    Optional<String> getRefreshToken();
}
