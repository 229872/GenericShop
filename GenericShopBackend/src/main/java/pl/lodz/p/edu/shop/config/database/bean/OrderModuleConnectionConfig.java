package pl.lodz.p.edu.shop.config.database.bean;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import pl.lodz.p.edu.shop.config.database.listener.OrdersModuleTxLogsListener;
import pl.lodz.p.edu.shop.config.database.preconfig.JpaConfigurable;
import pl.lodz.p.edu.shop.config.database.property.DataSourceProperties;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(
    entityManagerFactoryRef = "ordersModEmFactory",
    transactionManagerRef = "ordersModTxManager",
    basePackages = {"pl.lodz.p.edu.shop.dataaccess.repository.module.order"}
)
public class OrderModuleConnectionConfig implements JpaConfigurable {

    public static final String PROPERTIES_ROOT = "app.db.datasource.orders-module";

    @Override
    @ConfigurationProperties(PROPERTIES_ROOT)
    @Bean("ordersModDataSourceProperties")
    public DataSourceProperties dataSourcePropertiesBean() {
        return JpaConfigurable.super.dataSourcePropertiesBean();
    }

    @Override
    @Bean("ordersModDataSource")
    public DataSource dataSourceBean(@Qualifier("ordersModDataSourceProperties") DataSourceProperties properties) {
        return JpaConfigurable.super.dataSourceBean(properties);
    }

    @Override
    @Bean("ordersModDataSourceInitializer")
    public DataSourceInitializer dataSourceInitializerBean(@Qualifier("ordersModDataSource") DataSource dataSource,
                                                           @Qualifier("ordersModDataSourceProperties") DataSourceProperties properties) {
        return JpaConfigurable.super.dataSourceInitializerBean(dataSource, properties);
    }

    @Override
    public Properties getJpaProperties(@Qualifier("ordersModDataSourceProperties") DataSourceProperties dataSourceProperties) {
        Properties jpaProperties = JpaConfigurable.super.getJpaProperties(dataSourceProperties);
        jpaProperties.putAll(dataSourceProperties.getJpa().getProperties());
        return jpaProperties;
    }

    @Override
    @Bean("ordersModEmFactory")
    public LocalContainerEntityManagerFactoryBean emFactoryBean(@Qualifier("ordersModDataSource") DataSource dataSource,
                                                                @Qualifier("ordersModDataSourceProperties") DataSourceProperties properties) {
        return JpaConfigurable.super.emFactoryBean(dataSource, properties);
    }

    @Override
    @Bean("ordersModTxManager")
    public JpaTransactionManager txManagerBean(@Qualifier("ordersModEmFactory") LocalContainerEntityManagerFactoryBean emFactory) {
        JpaTransactionManager txManager = JpaConfigurable.super.txManagerBean(emFactory);
        txManager.addListener(new OrdersModuleTxLogsListener());
        return txManager;
    }
}