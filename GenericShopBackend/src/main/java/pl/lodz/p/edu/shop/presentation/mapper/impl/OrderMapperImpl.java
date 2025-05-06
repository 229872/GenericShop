package pl.lodz.p.edu.shop.presentation.mapper.impl;

import org.springframework.stereotype.Component;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Order;
import pl.lodz.p.edu.shop.logic.service.api.VersionSignatureVerifier;
import pl.lodz.p.edu.shop.presentation.dto.order.OrderOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.product.ProductOutputDto;
import pl.lodz.p.edu.shop.presentation.mapper.api.OrderMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Component
class OrderMapperImpl implements OrderMapper {

    private final VersionSignatureVerifier verifier;

    OrderMapperImpl(VersionSignatureVerifier verifier) {
        this.verifier = requireNonNull(verifier);
    }

    @Override
    public OrderOutputDto mapToMinimalOrderOutputDTO(Order order) {
        LocalDateTime notParsedCreationDate = order.getCreatedAt();
        String creationDate = formatDate(notParsedCreationDate);

        return OrderOutputDto.builder()
            .id(order.getId())
            .version(verifier.signVersion(order.getVersion()))
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
            .version(verifier.signVersion(order.getVersion()))
            .products(mappedProducts)
            .totalPrice(order.getTotalPrice())
            .accountId(order.getAccount().getId())
            .creationDate(creationDate)
            .productQuantity(order.getOrderedProducts().size())
            .build();
    }

    private String formatDate(LocalDateTime notParsedCreationDate) {
        return notParsedCreationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
