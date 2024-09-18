package pl.lodz.p.edu.shop.dataaccess.repository.module.order;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Order;
import pl.lodz.p.edu.shop.dataaccess.repository.api.OrderRepository;

interface OrderJpaRepository extends JpaRepository<Order, Long>, OrderRepository {

}
