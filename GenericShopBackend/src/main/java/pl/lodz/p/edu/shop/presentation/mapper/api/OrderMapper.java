package pl.lodz.p.edu.shop.presentation.mapper.api;

import pl.lodz.p.edu.shop.dataaccess.model.entity.Order;
import pl.lodz.p.edu.shop.presentation.dto.order.OrderOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.product.ProductOutputDto;

import java.util.List;

public interface OrderMapper {

    OrderOutputDto mapToMinimalOrderOutputDTO(Order order);

    OrderOutputDto mapToOrderOutputDTOWithFullInformation(Order order, List<ProductOutputDto> mappedProducts);
}
