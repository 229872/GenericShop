package pl.lodz.p.edu.shop.presentation.mapper.impl;

import org.springframework.stereotype.Component;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Order;
import pl.lodz.p.edu.shop.presentation.dto.order.OrderOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.product.ProductOutputDto;
import pl.lodz.p.edu.shop.presentation.mapper.api.OrderMapper;
import pl.lodz.p.edu.shop.util.SecurityUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderOutputDto mapToMinimalOrderOutputDTO(Order order) {
        LocalDateTime notParsedCreationDate = order.getCreatedAt();
        String creationDate = formatDate(notParsedCreationDate);

        return OrderOutputDto.builder()
            .id(order.getId())
            .version(SecurityUtil.signVersion(order.getVersion()))
            .totalPrice(order.getTotalPrice())
            .accountId(order.getAccount().getId())
            .creationDate(creationDate)
            .build();
    }

    @Override
    public OrderOutputDto mapToOrderOutputDTOWithFullInformation(Order order, List<ProductOutputDto> mappedProducts) {
        LocalDateTime notParsedCreationDate = order.getCreatedAt();
        String creationDate = formatDate(notParsedCreationDate);

        return OrderOutputDto.builder()
            .id(order.getId())
            .version(SecurityUtil.signVersion(order.getVersion()))
            .products(mappedProducts)
            .totalPrice(order.getTotalPrice())
            .accountId(order.getAccount().getId())
            .creationDate(creationDate)
            .productQuantity(order.getOrderedProducts().size())
            .build();
    }

    private String formatDate(LocalDateTime notParsedCreationDate) {
        return notParsedCreationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"));
    }
}
