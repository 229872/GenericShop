package pl.lodz.p.edu.integration.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import pl.lodz.p.edu.config.JwtFilter;

@RequiredArgsConstructor

@Configuration
@Profile("it")
public class IntegrationTestSecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity = httpSecurity
            .csrf().disable()
            .authorizeHttpRequests(a -> a.anyRequest().permitAll());

        return httpSecurity.build();
    }

}
