package pl.lodz.p.edu.shop.logic.service.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Order;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Rate;

import java.util.Map;

public interface OrderService {

    Order placeAndOrder(String login, Map<Long, Integer> productsForOrder);

    Page<Order> findAll(String login, Pageable pageable);

    Page<Order> findAllByUserLogin(String login, Pageable pageable);

    Order findOrderById(String login, Long id);

    Rate rateOrderedProduct(String login, Long orderedProductId, Integer rateValue);

    Rate reRateOrderedProduct(String login, Long orderedProductId, Integer rateValue);

    void removeRate(String login, Long orderedProductId);
}
