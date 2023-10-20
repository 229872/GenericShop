package pl.lodz.p.edu.logic.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.lodz.p.edu.dataaccess.model.Account;
import pl.lodz.p.edu.dataaccess.repository.api.AccountRepository;
import pl.lodz.p.edu.exception.ExceptionFactory;
import pl.lodz.p.edu.logic.model.JwtTokens;
import pl.lodz.p.edu.logic.service.api.AuthenticationService;
import pl.lodz.p.edu.logic.service.api.JwtService;

@RequiredArgsConstructor

@Service
@Qualifier("AuthenticationServiceImpl")
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public JwtTokens authenticate(String login, String password) {
        Account account = accountRepository.findByLogin(login)
            .orElseThrow(ExceptionFactory::createInvalidCredentialsException);

        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw ExceptionFactory.createInvalidCredentialsException();
        }

        String jwtToken = jwtService.generateToken(login, account.getAccountRoles());
        return JwtTokens.builder().token(jwtToken).build();
    }
}
