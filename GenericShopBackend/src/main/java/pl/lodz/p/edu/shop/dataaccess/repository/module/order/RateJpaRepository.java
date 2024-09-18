package pl.lodz.p.edu.shop.dataaccess.repository.module.order;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Rate;
import pl.lodz.p.edu.shop.dataaccess.repository.api.RateRepository;

interface RateJpaRepository extends JpaRepository<Rate, Long>, RateRepository {
}
