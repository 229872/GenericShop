package pl.lodz.p.edu.shop.presentation.dto.user.log;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AuthLogOutputDto(
    String lastSuccessfulAuthIpAddr,
    String lastUnsuccessfulAuthIpAddr,
    LocalDateTime lastSuccessfulAuthTime,
    LocalDateTime lastUnsuccessfulAuthTime,
    Integer unsuccessfulAuthCounter,
    LocalDateTime blockadeEndTime
) {
}
