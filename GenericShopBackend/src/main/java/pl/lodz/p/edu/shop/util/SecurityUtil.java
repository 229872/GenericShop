package pl.lodz.p.edu.shop.util;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.Principal;
import java.util.Base64;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Configuration
public class SecurityUtil {

    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    private static String SECRET_KEY;

    @Configuration
    @ConfigurationProperties("app.security.version")
    @Data
    @Validated
    static class SecurityUtilProps {
        @NotBlank(message = "app.security.version.key is missing set this property to use application")
        private String key;
    }

    @Autowired
    void setSecretKey(SecurityUtilProps props) {
        SECRET_KEY = props.getKey();
    }



    public static String getLoginFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(authentication).map(Principal::getName).orElse("GUEST");
    }

    public static String signVersion(Long version) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(SECRET_KEY.getBytes(), HMAC_SHA256_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(version.toString().getBytes());
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC: " + e.getMessage());
        }
    }

    public static boolean verifySignature(Long version, String signature) {
        String generatedSignature = signVersion(version);
        return generatedSignature.equals(signature);
    }
}
