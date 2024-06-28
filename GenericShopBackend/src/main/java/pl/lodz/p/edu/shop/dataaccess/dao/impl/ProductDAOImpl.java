package pl.lodz.p.edu.shop.dataaccess.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.edu.shop.dataaccess.dao.api.ProductDAO;

import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import static java.util.Objects.requireNonNull;

@Component
@Transactional(transactionManager = "ordersModTxManager", propagation = Propagation.MANDATORY)
@Slf4j
class ProductDAOImpl implements ProductDAO {

    private final JdbcTemplate jdbcTemplate;

    public ProductDAOImpl(@Qualifier("ordersModJdbcTemplate") JdbcTemplate jdbcTemplate) {
        requireNonNull(jdbcTemplate, "ProductDAO requires non null jdbcTemplate");
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<String, Object> insert(String tableName, Map<String, Object> record) {
        Set<String> columnNames = record.keySet();
        StringJoiner columns = new StringJoiner(", ");
        StringJoiner valuesPlaceholder = new StringJoiner(", ");

        columnNames.forEach(column -> {
            columns.add(column);
            valuesPlaceholder.add("?");
        });

        String sql = "INSERT INTO %s (%s) VALUES (%s);".formatted(tableName, columns.toString(), valuesPlaceholder.toString());
        Object[] values = record.values().toArray();
        
        jdbcTemplate.update(sql, values);

        return record;
    }
}
