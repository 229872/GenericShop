package pl.lodz.p.edu.shop.presentation.adapter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Order;
import pl.lodz.p.edu.shop.logic.service.api.OrderService;
import pl.lodz.p.edu.shop.presentation.adapter.api.OrderServiceOperations;
import pl.lodz.p.edu.shop.presentation.dto.order.CreateOrderDTO;
import pl.lodz.p.edu.shop.presentation.dto.order.OrderOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.product.ProductRequest;
import pl.lodz.p.edu.shop.util.SecurityUtil;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor

@Component
public class OrderServiceAdapter implements OrderServiceOperations {

    private final OrderService orderService;

    @Override
    public OrderOutputDto placeAnOrder(String login, CreateOrderDTO newOrder) {
        Map<Long, Integer> productsForOrder = newOrder.productsRequest().stream()
            .collect(toMap(
                ProductRequest::id, ProductRequest::quantity
            ));

        Order order = orderService.placeAndOrder(login, productsForOrder);

        return OrderOutputDto.builder()
            .id(order.getId())
            .version(SecurityUtil.signVersion(order.getVersion()))
            .totalPrice(order.getTotalPrice())
            .accountId(order.getAccount().getId())
            .build();
    }
}
