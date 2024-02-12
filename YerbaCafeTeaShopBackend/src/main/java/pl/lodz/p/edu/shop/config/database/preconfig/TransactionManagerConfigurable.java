package pl.lodz.p.edu.shop.config.database.preconfig;

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

public interface TransactionManagerConfigurable {

    /**
     * @apiNote Override this method and annotate with @Bean with unique name and use @Qualifier for parameters
     */
    default JpaTransactionManager txManagerBean(LocalContainerEntityManagerFactoryBean emFactory) {
        return new JpaTransactionManager(emFactory.getObject());
    }
}
