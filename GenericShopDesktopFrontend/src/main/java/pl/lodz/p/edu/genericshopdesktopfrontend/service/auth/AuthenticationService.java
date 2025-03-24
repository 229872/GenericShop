package pl.lodz.p.edu.genericshopdesktopfrontend.service.auth;

import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.Role;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.Tokens;

import java.util.Set;

public sealed interface AuthenticationService permits AuthenticationServiceImpl {

    static AuthenticationService getInstance() {
        return AuthenticationServiceImpl.INSTANCE;
    }

    void authenticate(Tokens tokens) throws ApplicationException;

    void logout();

    Set<Role> getAccountRoles();

    Role getActiveRole();

    Role setActiveRole(Role newActiveRole);
}
