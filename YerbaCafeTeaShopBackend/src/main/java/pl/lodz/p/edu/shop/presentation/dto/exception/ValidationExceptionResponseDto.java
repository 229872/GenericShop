package pl.lodz.p.edu.shop.presentation.dto.exception;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record ValidationExceptionResponseDto(
    int status,
    String error,
    Map<String, List<String>> messages
) {
}
