package pl.lodz.p.edu.genericshopdesktopfrontend.service.auth;

import pl.lodz.p.edu.genericshopdesktopfrontend.model.Role;

import java.util.Optional;

enum AuthenticationServiceImpl implements AuthenticationService {

    INSTANCE;

    private Role activeRole = Role.GUEST;

    private String authToken;

    private Optional<String> authToken() {
        return Optional.ofNullable(authToken);
    }

    private String refreshToken;

    private Optional<String> refreshToken() {
        return Optional.ofNullable(refreshToken);
    }





}
