package pl.lodz.p.edu.shop.logic.service.api;

import pl.lodz.p.edu.shop.dataaccess.model.entity.Order;

import java.util.Map;

public interface OrderService {

    Order placeAndOrder(String login, Map<Long, Integer> productsForOrder);
}
