package pl.lodz.p.edu.shop.dataaccess.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.edu.shop.dataaccess.dao.api.CategoryDAO;
import pl.lodz.p.edu.shop.dataaccess.model.other.Constraint;

import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@Slf4j

@Component
@Transactional(transactionManager = "ordersModTxManager", propagation = Propagation.MANDATORY)
class CategoryDAOImpl implements CategoryDAO {

    private final JdbcTemplate jdbcTemplate;

    CategoryDAOImpl(JdbcTemplate jdbcTemplate) {
        requireNonNull(jdbcTemplate, "CategoryDAO requires non null jdbcTemplate");
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<String, List<Constraint>> createTable(String tableName, Map<String, List<Constraint>> schema) {
        StringBuilder stringBuilder = new StringBuilder("CREATE TABLE %s ( ".formatted(tableName));

        schema.forEach((key, value) -> {
            stringBuilder.append(key);
            stringBuilder.append(" ");
            value.forEach(v -> stringBuilder.append(v.getConstraintValue()).append(" "));
            stringBuilder.append(", ");
        });

        stringBuilder.append("product_id BIGINT, FOREIGN KEY (product_id) REFERENCES products(id) );");
        String createTableSql = stringBuilder.toString();

        jdbcTemplate.execute(createTableSql);
        return schema;
    }

    @Override
    public List<Map<String, Object>> findTableSchema(String tableName) {
        String sql = "SELECT column_name, data_type, character_maximum_length, is_nullable " +
            "FROM information_schema.columns " +
            "WHERE table_name = ?";

        return jdbcTemplate.queryForList(sql, tableName);
    }
}
