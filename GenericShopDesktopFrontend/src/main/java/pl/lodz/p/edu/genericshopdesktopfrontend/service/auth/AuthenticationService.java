package pl.lodz.p.edu.genericshopdesktopfrontend.service.auth;

public sealed interface AuthenticationService permits AuthenticationServiceImpl {

    static AuthenticationService getInstance() {
        return AuthenticationServiceImpl.INSTANCE;
    }


}
