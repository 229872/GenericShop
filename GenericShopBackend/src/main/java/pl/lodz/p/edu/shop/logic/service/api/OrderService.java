package pl.lodz.p.edu.shop.logic.service.api;

import pl.lodz.p.edu.shop.dataaccess.model.entity.Order;
import pl.lodz.p.edu.shop.logic.model.ProductForOrder;

import java.util.List;

public interface OrderService {

    Order placeAndOrder(String login, List<ProductForOrder> productsForOrder);
}
