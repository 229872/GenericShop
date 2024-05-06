package pl.lodz.p.edu.shop.config;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class PostgresqlContainerSetup {

    private static final PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("shop")
        .withUsername("shop_admin")
        .withPassword("test")
        .withReuse(true)
        .withInitScript("script/init-users.sql");

    @BeforeAll
    static void beforeAll() {
        database.start();
    }

    @DynamicPropertySource
    private static void testPropertiesForPostgresql(DynamicPropertyRegistry registry) {
        registry.add("app.db.datasource.init-module.url", database::getJdbcUrl);
        registry.add("app.db.datasource.init-module.username", database::getUsername);
        registry.add("app.db.datasource.init-module.password", database::getPassword);
        registry.add("app.db.datasource.accounts-module.url", database::getJdbcUrl);
    }

}
