package pl.lodz.p.edu.shop.presentation.dto.preference;

import java.util.Map;

public record UserPreferencesDto(
    Map<String, Double> categoryPreferences,
    Map<Long, Double> productPreferences
) {
}
