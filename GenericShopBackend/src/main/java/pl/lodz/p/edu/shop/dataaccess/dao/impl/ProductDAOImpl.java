package pl.lodz.p.edu.shop.dataaccess.dao.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.edu.shop.dataaccess.dao.api.ProductDAO;

import java.util.Map;

import static java.util.Objects.requireNonNull;

@Component
@Transactional(transactionManager = "ordersModTxManager", propagation = Propagation.MANDATORY)
class ProductDAOImpl implements ProductDAO {

    private final JdbcTemplate jdbcTemplate;

    public ProductDAOImpl(@Qualifier("ordersModJdbcTemplate") JdbcTemplate jdbcTemplate) {
        requireNonNull(jdbcTemplate, "ProductDAO requires non null jdbcTemplate");
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<String, Object> insert(String tableName, Map<String, Object> record) {
        return Map.of();
    }
}
