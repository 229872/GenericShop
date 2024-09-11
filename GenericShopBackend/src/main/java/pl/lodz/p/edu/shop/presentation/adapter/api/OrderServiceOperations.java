package pl.lodz.p.edu.shop.presentation.adapter.api;

import pl.lodz.p.edu.shop.presentation.dto.order.CreateOrderDTO;
import pl.lodz.p.edu.shop.presentation.dto.order.OrderOutputDto;

public interface OrderServiceOperations {

    OrderOutputDto placeAnOrder(String login, CreateOrderDTO newOrder);

}
