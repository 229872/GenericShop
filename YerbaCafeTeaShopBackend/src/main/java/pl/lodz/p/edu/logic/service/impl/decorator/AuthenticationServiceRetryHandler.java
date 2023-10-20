package pl.lodz.p.edu.logic.service.impl.decorator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import pl.lodz.p.edu.logic.model.JwtTokens;
import pl.lodz.p.edu.logic.service.AbstractRetryHandler;
import pl.lodz.p.edu.logic.service.api.AuthenticationService;

@Service
@Primary
public class AuthenticationServiceRetryHandler extends AbstractRetryHandler implements AuthenticationService {

    private final AuthenticationService authenticationService;

    public AuthenticationServiceRetryHandler(@Qualifier("AuthenticationServiceImpl") AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public JwtTokens authenticate(String login, String password) {
        return repeatTransactionWhenTimeoutOccurred(() -> authenticationService.authenticate(login, password));
    }
}
