package pl.lodz.p.edu.presentation.dto.exception;

import lombok.Builder;

import java.util.List;

@Builder
public record ValidationExceptionResponseDto(
    int status,
    String error,
    List<String> messages
) {
}
