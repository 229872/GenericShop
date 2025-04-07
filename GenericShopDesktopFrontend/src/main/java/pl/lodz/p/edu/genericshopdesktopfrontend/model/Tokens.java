package pl.lodz.p.edu.genericshopdesktopfrontend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Tokens(
    @JsonProperty("token") String authToken,
    String refreshToken) {
}
