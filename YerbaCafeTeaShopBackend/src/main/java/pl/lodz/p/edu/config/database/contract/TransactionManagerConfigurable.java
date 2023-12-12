package pl.lodz.p.edu.config.database.contract;

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
