package pl.lodz.p.edu.shop.dataaccess.repository.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Order;

import java.util.Optional;

public interface OrderRepository {

    Page<Order> findAll(Pageable pageable);

    Optional<Order> findById(Long id);

    Order save(Order order);

    Order saveAndFlush(Order order);
}
