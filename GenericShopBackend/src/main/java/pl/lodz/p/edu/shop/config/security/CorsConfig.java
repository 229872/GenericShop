package pl.lodz.p.edu.shop.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import pl.lodz.p.edu.shop.config.frontend.property.FrontendProperties;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CorsConfig {

    private final FrontendProperties frontendProperties;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200", frontendProperties.getFrontendAppUrl()));
        corsConfiguration.setAllowedHeaders(Arrays.asList(
            "Origin", "Access-Control-Allow-Origin", "Content-Type", "Accept", "Authorization", "Origin, Accept",
            "X-Requested-With", "Access-Control-Request-Method", "Access-Control-Request-Headers"
        ));
        corsConfiguration.setExposedHeaders(Arrays.asList(
            "Origin", "Content-Type", "Accept", "Authorization", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"
        ));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
