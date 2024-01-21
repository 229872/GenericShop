package pl.lodz.p.edu.config.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Slf4j

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class AccessLogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        Instant beginTime = Instant.now();
        String ipAddress = "";
        try {
            ipAddress = Optional.ofNullable(request.getHeader("X-FORWARDED-FOR"))
                .filter(addr -> !addr.isBlank())
                .orElse(request.getRemoteAddr());

            log.info("Request from {}: {} {} {}", ipAddress, request.getMethod(), request.getRequestURI(), request.getContentType());
            filterChain.doFilter(request, response);

        } finally {
            Duration duration = Duration.between(beginTime, Instant.now());
            log.info("Response to ({} {}) from {} executed with status {} in {}ms",
                request.getMethod(), request.getRequestURI(), ipAddress,  response.getStatus(), duration.toMillis());
        }
    }
}
