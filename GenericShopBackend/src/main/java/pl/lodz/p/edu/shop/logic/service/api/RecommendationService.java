package pl.lodz.p.edu.shop.logic.service.api;

import pl.lodz.p.edu.shop.dataaccess.model.entity.Product;
import pl.lodz.p.edu.shop.logic.model.UserPreferences;

import java.util.List;

public interface RecommendationService {

    List<Product> findByRecommendation(String login, UserPreferences userPreferences, Integer numberOfRecords);
}
