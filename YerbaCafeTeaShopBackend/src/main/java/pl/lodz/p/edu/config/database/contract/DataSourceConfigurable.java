package pl.lodz.p.edu.config.database.contract;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import pl.lodz.p.edu.config.database.property.DataSourceProperties;

import javax.sql.DataSource;
import java.util.List;

public interface DataSourceConfigurable {

    /**
     * @apiNote Override this method and annotate with @Bean with unique and use @ConfigurationProperties
     */
    default DataSourceProperties dataSourcePropertiesBean() {
        return new DataSourceProperties();
    }

    /**
     * @apiNote Override this method and annotate with @Bean with unique name and use @Qualifier for parameters
     */
    default DataSource dataSourceBean(DataSourceProperties properties) {
        return DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .url(properties.getUrl())
            .username(properties.getUsername())
            .password(properties.getPassword())
            .build();
    }

    /**
     * @apiNote Override this method and annotate with @Bean with unique name and use @Qualifier for parameters
     */
    default DataSourceInitializer dataSourceInitializerBean(DataSource dataSource, DataSourceProperties properties) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();

        Boolean isEnabled = properties.getSql().isEnable();
        List<String> scriptsLocations = properties.getSql().getDataLocations();

        var dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(populator);

        if (isEnabled) {
            scriptsLocations.forEach(location -> populator.addScript(new ClassPathResource(location)));
            dataSourceInitializer.setEnabled(true);
        } else {
            dataSourceInitializer.setEnabled(false);
        }

        return dataSourceInitializer;
    }
}
