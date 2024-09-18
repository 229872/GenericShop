package pl.lodz.p.edu.shop.dataaccess.repository.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Page<Order> findAll(Pageable pageable);

    Optional<Order> findById(Long id);

    @Query("SELECT o FROM Order o JOIN FETCH o.orderedProducts WHERE o.id = :id")
    Optional<Order> findByIdWithOrderedProducts(@Param("id") Long id);

    @EntityGraph(attributePaths = "orderedProducts")
    @Query("SELECT o FROM Order o WHERE o.account.login = :login")
    Page<Order> findAllByAccountLogin(@Param("login") String login, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN FETCH o.orderedProducts WHERE o.account.login = :login")
    List<Order> findAllByAccountLogin(@Param("login") String login);

    Order save(Order order);

    Order saveAndFlush(Order order);
}
