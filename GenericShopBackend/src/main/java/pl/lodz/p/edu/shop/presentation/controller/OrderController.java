package pl.lodz.p.edu.shop.presentation.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping
    @RolesAllowed({CLIENT})
    public ResponseEntity<Page<OrderOutputDto>> findAll(Pageable pageable) {
        String login = getLoginFromSecurityContext();

        Page<OrderOutputDto> responseBody = orderService.findAll(login, pageable);
        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/id/{id}")
    @RolesAllowed({CLIENT})
    public ResponseEntity<OrderOutputDto> findById(@PathVariable("id") Long id) {
        String login = getLoginFromSecurityContext();

        OrderOutputDto responseBody = orderService.findById(login, id);
        return ResponseEntity.ok(responseBody);
    }
}
