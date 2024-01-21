package pl.lodz.p.edu;

import jakarta.annotation.security.DeclareRoles;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

import static pl.lodz.p.edu.config.security.role.RoleName.*;

@SpringBootApplication(exclude = {
	DataSourceAutoConfiguration.class,
	DataSourceTransactionManagerAutoConfiguration.class,
	HibernateJpaAutoConfiguration.class,
	XADataSourceAutoConfiguration.class,
	UserDetailsServiceAutoConfiguration.class
})
@DeclareRoles({GUEST, ADMIN, CLIENT, EMPLOYEE})
public class YerbaCafeTeaShopBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(YerbaCafeTeaShopBackendApplication.class, args);
	}

}
