package pl.lodz.p.edu.shop.logic.service.impl;

import org.springframework.stereotype.Component;
import pl.lodz.p.edu.shop.config.security.property.SecurityUtilProps;
import pl.lodz.p.edu.shop.logic.service.api.VersionSignatureVerifier;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import static java.util.Objects.requireNonNull;

@Component
class VersionSignatureVerifierImpl implements VersionSignatureVerifier {

    private final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    private final String SECRET_KEY;

    VersionSignatureVerifierImpl(SecurityUtilProps props) {
        SECRET_KEY = requireNonNull(props.getKey());
    }

    @Override
    public String signVersion(Long version) {
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

    @Override
    public boolean verifySignature(Long version, String signature) {
        String generatedSignature = signVersion(version);
        return generatedSignature.equals(signature);
    }
}
