package pl.lodz.p.edu.shop.config.database.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Category;
import pl.lodz.p.edu.shop.dataaccess.repository.api.CategoryRepository;

import java.util.List;

@Slf4j
public class DatabaseCleanupOnShutdown implements DisposableBean {

    private final JdbcTemplate jdbcTemplate;
    private final CategoryRepository categoryRepository;

    public DatabaseCleanupOnShutdown(@Qualifier("initModJdbcTemplate") JdbcTemplate jdbcTemplate, CategoryRepository categoryRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void destroy() throws Exception {
        log.info("Deleting jdbc created tables");
        List<String> tableList = categoryRepository.findAll().stream()
            .map(Category::getName)
            .map(name -> "%ss".formatted(name.toLowerCase()))
            .toList();
        dropTables(tableList);
    }

    private void dropTables(List<String> tableNames) {
        tableNames.forEach(tableName -> {
            String sql = String.format("DROP TABLE %s CASCADE", tableName);
            try {
                jdbcTemplate.execute(sql);
                log.info("Dropped table: {}", tableName);
            } catch (Exception e) {
                log.error("Failed to drop table: {}", tableName, e);
            }
        });
    }
}