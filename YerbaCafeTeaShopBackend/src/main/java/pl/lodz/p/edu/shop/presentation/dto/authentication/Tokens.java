package pl.lodz.p.edu.shop.presentation.dto.authentication;

import lombok.Builder;

@Builder
public record Tokens(
    String token,
    String refreshToken
) {
}
