package pl.lodz.p.edu.shop.presentation.adapter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Order;
import pl.lodz.p.edu.shop.logic.service.api.OrderService;
import pl.lodz.p.edu.shop.presentation.adapter.api.OrderServiceOperations;
import pl.lodz.p.edu.shop.presentation.dto.order.CreateOrderDTO;
import pl.lodz.p.edu.shop.presentation.dto.order.OrderOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.product.ProductOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.product.ProductRequest;
import pl.lodz.p.edu.shop.presentation.mapper.api.OrderMapper;
import pl.lodz.p.edu.shop.presentation.mapper.api.ProductMapper;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor

@Component
class OrderServiceAdapter implements OrderServiceOperations {

    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;

    @Override
    public OrderOutputDto placeAnOrder(String login, CreateOrderDTO newOrder) {
        Map<Long, Integer> productsForOrder = newOrder.productsRequest().stream()
            .collect(toMap(
                ProductRequest::id, ProductRequest::quantity
            ));

        Order order = orderService.placeAndOrder(login, productsForOrder);
        return orderMapper.mapToMinimalOrderOutputDTO(order);
    }

    @Override
    public Page<OrderOutputDto> findAll(String login, Pageable pageable) {
        return orderService.findAll(login, pageable)
            .map(orderMapper::mapToMinimalOrderOutputDTO);
    }

    @Override
    public OrderOutputDto findById(String login, Long id) {
        Order order = orderService.findOrderById(login, id);
        List<ProductOutputDto> mappedProducts = order.getOrderedProducts().stream()
            .map(productMapper::mapToProductOutputDtoWithoutVersion)
            .toList();

        return orderMapper.mapToOrderOutputDTOWithFullInformation(order, mappedProducts);
    }
}
