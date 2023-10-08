package pl.lodz.p.edu.presentation.dto.exception;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ExceptionResponseDto(
    int status,
    LocalDateTime timestamp,
    String message
) {
}
