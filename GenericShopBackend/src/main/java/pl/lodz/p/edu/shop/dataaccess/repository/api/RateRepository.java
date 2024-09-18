package pl.lodz.p.edu.shop.dataaccess.repository.api;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Rate;

public interface RateRepository {

    Rate save(Rate rate);

    void delete(Rate rate);

    @Query("SELECT COALESCE(avg(r.value), 0.0) FROM Rate r WHERE r.product.id = :productId")
    double findAverageRatingForProduct(@Param("productId") Long productId);
}
