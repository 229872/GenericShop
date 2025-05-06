package pl.lodz.p.edu.shop.logic.service.api;

public interface VersionSignatureVerifier {

    String signVersion(Long version);

    boolean verifySignature(Long version, String signature);
}
