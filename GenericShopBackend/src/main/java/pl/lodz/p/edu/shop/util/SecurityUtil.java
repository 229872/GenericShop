package pl.lodz.p.edu.shop.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SecurityUtil {

    public static String getLoginFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(authentication).map(Principal::getName).orElse("GUEST");
    }
}
