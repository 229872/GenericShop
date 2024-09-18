package pl.lodz.p.edu.shop.dataaccess.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.edu.shop.dataaccess.dao.api.ProductDAO;

import java.util.*;

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

    @Override
    public Map<String, Object> findByIdInTable(Long id, String tableName) {
        String sql = "SELECT * FROM %s WHERE product_id = ?".formatted(tableName);

        RowMapper<Map<String, Object>> mapper = (rs, rowNum) -> {
            Map<String, Object> result = new HashMap<>();
            int columnCount = rs.getMetaData().getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = rs.getMetaData().getColumnName(i);
                Object columnValue = rs.getObject(i);
                result.put(columnName, columnValue);
            }
            return result;
        };

        List<Map<String, Object>> result = jdbcTemplate.query(sql, mapper, id);
        Map<String, Object> data = !result.isEmpty() ? result.get(0) : Map.of();
        data.remove("product_id");
        return data;
    }
}
