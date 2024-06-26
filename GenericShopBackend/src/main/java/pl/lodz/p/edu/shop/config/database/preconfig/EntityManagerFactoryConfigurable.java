package pl.lodz.p.edu.shop.config.database.preconfig;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import pl.lodz.p.edu.shop.config.database.property.DataSourceProperties;

import javax.sql.DataSource;
import java.util.Properties;

public interface EntityManagerFactoryConfigurable {

    /**
     * @apiNote Override this method and annotate with @Bean with unique name and use @Qualifier for parameters
     * @apiNote You must set persistence unit name
     */
    default LocalContainerEntityManagerFactoryBean emFactoryBean(DataSource dataSource, DataSourceProperties properties) {
        var emFactory = new LocalContainerEntityManagerFactoryBean();

        emFactory.setPersistenceUnitName(properties.getPersistenceUnitName());
        emFactory.setDataSource(dataSource);
        emFactory.setPersistenceProvider(new HibernatePersistenceProvider());
        emFactory.setJpaProperties(getJpaProperties(properties));
        emFactory.setPackagesToScan("pl.lodz.p.edu.shop.dataaccess.model");

        return emFactory;
    }

    default Properties getJpaProperties(DataSourceProperties dataSourceProperties) {
        DataSourceProperties.JpaProperties properties = dataSourceProperties.getJpa();
        Properties jpaProperties = new Properties();
        jpaProperties.setProperty("jakarta.persistence.schema-generation.database.action", properties.getDatabaseAction().getActionName());
        return jpaProperties;
    }
}
