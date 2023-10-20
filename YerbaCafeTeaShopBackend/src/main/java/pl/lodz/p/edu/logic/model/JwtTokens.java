package pl.lodz.p.edu.logic.model;

import lombok.Builder;

@Builder
public record JwtTokens(
    String token,
    String refreshToken
) {
}
