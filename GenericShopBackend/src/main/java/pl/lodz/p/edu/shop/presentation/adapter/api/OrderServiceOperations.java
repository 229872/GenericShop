package pl.lodz.p.edu.shop.presentation.adapter.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.lodz.p.edu.shop.presentation.dto.order.CreateOrderDTO;
import pl.lodz.p.edu.shop.presentation.dto.order.OrderOutputDto;

public interface OrderServiceOperations {

    OrderOutputDto placeAnOrder(String login, CreateOrderDTO newOrder);

    Page<OrderOutputDto> findAll(String login, Pageable pageable);

    OrderOutputDto findById(String login, Long id);
}
