package pl.lodz.p.edu.config.database.preconfig;

public interface JpaConfigurable extends DataSourceConfigurable, EntityManagerFactoryConfigurable, TransactionManagerConfigurable {

    /**
     * Override all methods and make all requirements for extended apis.
     */
}
