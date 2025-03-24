package pl.lodz.p.edu.genericshopdesktopfrontend.service.auth;

import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.Tokens;

public sealed interface AuthenticationService permits AuthenticationServiceImpl {

    static AuthenticationService getInstance() {
        return AuthenticationServiceImpl.INSTANCE;
    }

    void authenticate(Tokens tokens) throws ApplicationException;

    void logout();
}
