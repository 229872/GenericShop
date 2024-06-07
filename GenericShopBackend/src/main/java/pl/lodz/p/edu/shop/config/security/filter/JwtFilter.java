package pl.lodz.p.edu.shop.config.security.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.lodz.p.edu.shop.logic.service.api.JwtService;
import pl.lodz.p.edu.shop.config.security.role.RoleName;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @SuppressWarnings("unchecked")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String BEARER = "Bearer";

        if (Objects.isNull(header) || !header.startsWith(BEARER)) {
            authorizeAsGuest();
        } else {
            try {
                String token = header.substring(BEARER.length());
                Claims claims = jwtService.validateAndExtractClaimsFromAuthToken(token);
                String login = claims.getSubject();
                List<String> roles = claims.get("accountRoles", List.class);

                authorizeWithRoles(login, roles);
            } catch (Exception e) {
                log.warn("Exception occurred in JwtFilter: ", e);
                authorizeAsGuest();
            }
        }
        filterChain.doFilter(request, response);
    }

    private void authorizeAsGuest() {
        authorizeWithRoles(RoleName.GUEST, List.of(RoleName.GUEST));
    }

    private void authorizeWithRoles(String login, List<String> roles) {
        String rolePrefix = "ROLE_";
        String[] authorities = roles.stream()
            .map(role -> rolePrefix + role)
            .toArray(String[]::new);

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(login, null, AuthorityUtils.createAuthorityList(authorities));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
