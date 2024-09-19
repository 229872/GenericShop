package pl.lodz.p.edu.shop.logic.model;

import java.util.Map;

public record UserPreferences(
    Map<String, Double> categoryPreferences,
    Map<Long, Double> productPreferences
) {
}
