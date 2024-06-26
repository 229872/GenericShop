package pl.lodz.p.edu.shop.config.database.bean;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import pl.lodz.p.edu.shop.config.database.listener.AccountsModuleTxLogsListener;
import pl.lodz.p.edu.shop.config.database.preconfig.JpaConfigurable;
import pl.lodz.p.edu.shop.config.database.property.DataSourceProperties;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(
    entityManagerFactoryRef = "accountsModEmFactory",
    transactionManagerRef = "accountsModTxManager",
    basePackages = {"pl.lodz.p.edu.shop.dataaccess.repository.module.account"}
)
public class AccountsModuleConnectionConfig implements JpaConfigurable {

    public static final String PROPERTIES_ROOT = "app.db.datasource.accounts-module";

    @Override
    @ConfigurationProperties(PROPERTIES_ROOT)
    @Bean("accountsModDataSourceProperties")
    public DataSourceProperties dataSourcePropertiesBean() {
        return JpaConfigurable.super.dataSourcePropertiesBean();
    }

    @Override
    @Bean("accountsModDataSource")
    public DataSource dataSourceBean(@Qualifier("accountsModDataSourceProperties") DataSourceProperties properties) {
        return JpaConfigurable.super.dataSourceBean(properties);
    }

    @Override
    @Bean("accountsModDataSourceInitializer")
    public DataSourceInitializer dataSourceInitializerBean(@Qualifier("accountsModDataSource") DataSource dataSource,
                                                           @Qualifier("accountsModDataSourceProperties") DataSourceProperties properties) {
        return JpaConfigurable.super.dataSourceInitializerBean(dataSource, properties);
    }

    @Override
    public Properties getJpaProperties(@Qualifier("accountsModDataSourceProperties") DataSourceProperties dataSourceProperties) {
        Properties jpaProperties = JpaConfigurable.super.getJpaProperties(dataSourceProperties);
        jpaProperties.putAll(dataSourceProperties.getJpa().getProperties());
        return jpaProperties;
    }

    @Override
    @Bean("accountsModEmFactory")
    public LocalContainerEntityManagerFactoryBean emFactoryBean(@Qualifier("accountsModDataSource") DataSource dataSource,
                                                                @Qualifier("accountsModDataSourceProperties") DataSourceProperties properties) {
        return JpaConfigurable.super.emFactoryBean(dataSource, properties);
    }

    @Override
    @Bean("accountsModTxManager")
    public JpaTransactionManager txManagerBean(@Qualifier("accountsModEmFactory") LocalContainerEntityManagerFactoryBean emFactory) {
        JpaTransactionManager txManager = JpaConfigurable.super.txManagerBean(emFactory);
        txManager.addListener(new AccountsModuleTxLogsListener());
        return txManager;
    }
}
