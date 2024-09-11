package pl.lodz.p.edu.shop.presentation.adapter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Order;
import pl.lodz.p.edu.shop.logic.model.ProductForOrder;
import pl.lodz.p.edu.shop.logic.service.api.OrderService;
import pl.lodz.p.edu.shop.presentation.adapter.api.OrderServiceOperations;
import pl.lodz.p.edu.shop.presentation.dto.order.CreateOrderDTO;
import pl.lodz.p.edu.shop.presentation.dto.order.OrderOutputDto;
import pl.lodz.p.edu.shop.util.SecurityUtil;

import java.util.List;

@RequiredArgsConstructor

@Component
public class OrderServiceAdapter implements OrderServiceOperations {

    private final OrderService orderService;

    @Override
    public OrderOutputDto placeAnOrder(String login, CreateOrderDTO newOrder) {
        List<ProductForOrder> productsForOrder = newOrder.productsRequest().stream()
            .map(productRequest -> new ProductForOrder(productRequest.id(), productRequest.quantity()))
            .toList();

        Order order = orderService.placeAndOrder(login, productsForOrder);

        return OrderOutputDto.builder()
            .id(order.getId())
            .version(SecurityUtil.signVersion(order.getVersion()))
            .totalPrice(order.getTotalPrice())
            .accountId(order.getAccount().getId())
            .build();
    }
}
