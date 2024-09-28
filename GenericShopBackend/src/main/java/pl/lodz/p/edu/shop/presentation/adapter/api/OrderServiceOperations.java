package pl.lodz.p.edu.shop.presentation.adapter.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.lodz.p.edu.shop.presentation.dto.order.CreateOrderDto;
import pl.lodz.p.edu.shop.presentation.dto.order.OrderOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.order.RateInputDto;
import pl.lodz.p.edu.shop.presentation.dto.order.RateOutputDto;

public interface OrderServiceOperations {

    OrderOutputDto placeAnOrder(String login, CreateOrderDto newOrder);

    Page<OrderOutputDto> findAll(String login, Pageable pageable);

    Page<OrderOutputDto> findAllByAccountLogin(String login, Pageable pageable);

    OrderOutputDto findById(String login, Long id);

    RateOutputDto rateOrderedProduct(String login, Long orderedProductId, RateInputDto rate);

    RateOutputDto reRateOrderedProduct(String login, Long orderedProductId, RateInputDto rate);

    void removeRate(String login, Long orderedProductId);
}
