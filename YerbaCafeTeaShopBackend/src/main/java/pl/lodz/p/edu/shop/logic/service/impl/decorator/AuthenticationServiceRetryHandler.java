package pl.lodz.p.edu.shop.logic.service.impl.decorator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.edu.shop.logic.model.JwtTokens;
import pl.lodz.p.edu.shop.logic.service.api.AuthenticationService;

@Service
@Primary
@Transactional(transactionManager = "accountsModTxManager", propagation = Propagation.NEVER)
@Qualifier("AuthenticationServiceRetryHandler")
class AuthenticationServiceRetryHandler extends AbstractRetryHandler implements AuthenticationService {

    private final AuthenticationService authenticationService;

    public AuthenticationServiceRetryHandler(@Qualifier("AuthenticationServiceImpl") AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public JwtTokens authenticate(String login, String password) {
        return repeatTransactionWhenTimeoutOccurred(() -> authenticationService.authenticate(login, password));
    }

    @Override
    public JwtTokens extendSession(String login, String refreshToken) {
        return repeatTransactionWhenTimeoutOccurred(() -> authenticationService.extendSession(login, refreshToken));
    }
}
