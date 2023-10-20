package pl.lodz.p.edu.presentation.dto.authentication;

import lombok.Builder;

@Builder
public record Tokens(
    String token,
    String refreshToken
) {
}
