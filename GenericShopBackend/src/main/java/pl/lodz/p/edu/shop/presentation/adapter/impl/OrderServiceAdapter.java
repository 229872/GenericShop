package pl.lodz.p.edu.shop.presentation.adapter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Order;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Rate;
import pl.lodz.p.edu.shop.logic.service.api.OrderService;
import pl.lodz.p.edu.shop.presentation.adapter.api.OrderServiceOperations;
import pl.lodz.p.edu.shop.presentation.dto.order.CreateOrderDto;
import pl.lodz.p.edu.shop.presentation.dto.order.OrderOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.order.RateInputDto;
import pl.lodz.p.edu.shop.presentation.dto.order.RateOutputDto;
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
    public OrderOutputDto placeAnOrder(String login, CreateOrderDto newOrder) {
        Map<Long, Integer> productsForOrder = newOrder.productsRequest().stream()
            .collect(toMap(
                ProductRequest::id, ProductRequest::quantity
            ));

        Order order = orderService.placeAndOrder(login, productsForOrder);
        return orderMapper.mapToMinimalOrderOutputDTO(order);
    }

    @Override
    public Page<OrderOutputDto> findAll(String login, Pageable pageable) {
        List<Sort.Order> orders = pageable.getSort().stream()
            .map(order -> order.getProperty().equals("creationDate") ?
                new Sort.Order(order.getDirection(), "createdAt") :
                order)
            .toList();
        Sort sort = Sort.by(orders);
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return orderService.findAll(login, pageRequest)
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

    @Override
    public RateOutputDto rateOrderedProduct(String login, Long productId, RateInputDto rate) {
        Rate newRate = orderService.rateOrderedProduct(login, productId, rate.rateValue());
        return new RateOutputDto(newRate.getValue());
    }

    @Override
    public RateOutputDto reRateOrderedProduct(String login, Long productId, RateInputDto rate) {
        Rate newRate = orderService.reRateOrderedProduct(login, productId, rate.rateValue());
        return new RateOutputDto(newRate.getValue());
    }

    @Override
    public void removeRate(String login, Long productGroupId) {
        orderService.removeRate(login, productGroupId);
    }
}
