package pl.lodz.p.edu.shop.config.database.bean;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import pl.lodz.p.edu.shop.config.database.listener.DatabaseCleanupOnShutdown;
import pl.lodz.p.edu.shop.config.database.preconfig.JpaConfigurable;
import pl.lodz.p.edu.shop.config.database.property.DataSourceProperties;
import pl.lodz.p.edu.shop.dataaccess.repository.api.CategoryRepository;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
    entityManagerFactoryRef = "initModEmFactory",
    transactionManagerRef = "initModTxManager",
    basePackages = {"pl.lodz.p.edu.shop.dataaccess.repository.module.init"}
)
public class InitModuleConnectionConfig implements JpaConfigurable {

    public static final String PROPERTIES_ROOT = "app.db.datasource.init-module";

    @Override
    @ConfigurationProperties(PROPERTIES_ROOT)
    @Bean("initModDataSourceProperties")
    public DataSourceProperties dataSourcePropertiesBean() {
        return JpaConfigurable.super.dataSourcePropertiesBean();
    }

    @Override
    @Bean("initModDataSource")
    public DataSource dataSourceBean(@Qualifier("initModDataSourceProperties") DataSourceProperties properties) {
        return JpaConfigurable.super.dataSourceBean(properties);
    }

    @Override
    @Bean("initModDataSourceInitializer")
    public DataSourceInitializer dataSourceInitializerBean(@Qualifier("initModDataSource") DataSource dataSource,
                                                           @Qualifier("initModDataSourceProperties") DataSourceProperties properties) {
        return JpaConfigurable.super.dataSourceInitializerBean(dataSource, properties);
    }

    @Override
    @Bean("initModEmFactory")
    public LocalContainerEntityManagerFactoryBean emFactoryBean(@Qualifier("initModDataSource") DataSource dataSource,
                                                                @Qualifier("initModDataSourceProperties") DataSourceProperties properties) {
        return JpaConfigurable.super.emFactoryBean(dataSource, properties);
    }

    @Override
    @Bean("initModTxManager")
    public JpaTransactionManager txManagerBean(@Qualifier("initModEmFactory") LocalContainerEntityManagerFactoryBean emFactory) {
        return JpaConfigurable.super.txManagerBean(emFactory);
    }

    @Bean("initModJdbcTemplate")
    public JdbcTemplate jdbcTemplateBean(@Qualifier("initModDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @ConditionalOnProperty(name = "app.db.datasource.init-module.jpa.database-action", havingValue = "drop_and_create")
    public DatabaseCleanupOnShutdown databaseCleanupOnShutdown(
        @Qualifier("initModJdbcTemplate") JdbcTemplate jdbcTemplate, CategoryRepository categoryRepository) {
        return new DatabaseCleanupOnShutdown(jdbcTemplate, categoryRepository);
    }
}
