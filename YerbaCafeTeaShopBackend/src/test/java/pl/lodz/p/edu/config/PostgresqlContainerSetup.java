package pl.lodz.p.edu.config;

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
        .withReuse(true);

    @BeforeAll
    static void beforeAll() {
        database.start();
    }

    @DynamicPropertySource
    private static void testPropertiesForPostgresql(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.username", database::getUsername);
        registry.add("spring.datasource.password", database::getPassword);
    }

}
