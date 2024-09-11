package pl.lodz.p.edu.shop.presentation.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.edu.shop.presentation.adapter.api.OrderServiceOperations;
import pl.lodz.p.edu.shop.presentation.dto.order.CreateOrderDTO;
import pl.lodz.p.edu.shop.presentation.dto.order.OrderOutputDto;

import java.net.URI;

import static pl.lodz.p.edu.shop.config.security.role.RoleName.CLIENT;
import static pl.lodz.p.edu.shop.util.SecurityUtil.getLoginFromSecurityContext;

@RequiredArgsConstructor

@RestController
@RequestMapping(ApiRoot.API_ROOT + "/orders")
public class OrderController {

    private final OrderServiceOperations orderService;

    @PostMapping
    @RolesAllowed({CLIENT})
    public ResponseEntity<OrderOutputDto> placeAnOrder(@Valid @RequestBody CreateOrderDTO newOrder) {
        String login = getLoginFromSecurityContext();
        OrderOutputDto responseBody = orderService.placeAnOrder(login, newOrder);

        URI resourceUri = URI.create("/id/%d".formatted(responseBody.id()));
        return ResponseEntity.created(resourceUri).body(responseBody);
    }
}
