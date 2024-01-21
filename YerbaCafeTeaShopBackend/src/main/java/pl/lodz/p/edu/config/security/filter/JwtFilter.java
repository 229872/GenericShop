package pl.lodz.p.edu.config.security.filter;

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
import pl.lodz.p.edu.logic.service.api.JwtService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static pl.lodz.p.edu.config.security.role.RoleName.GUEST;

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
                Claims claims = jwtService.getTokenClaims(token).getBody();
                String login = claims.getSubject();
                List<String> roles = claims.get("roles", List.class);

                authorizeWithRoles(login, roles);
            } catch (Exception e) {
                log.warn("Exception occurred in JwtFilter: ", e);
                authorizeAsGuest();
            }
        }
        filterChain.doFilter(request, response);
    }

    private void authorizeAsGuest() {
        authorizeWithRoles(GUEST, List.of(GUEST));
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
