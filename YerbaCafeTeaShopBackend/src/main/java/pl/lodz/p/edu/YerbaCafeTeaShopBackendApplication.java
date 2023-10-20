package pl.lodz.p.edu;

import jakarta.annotation.security.DeclareRoles;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static pl.lodz.p.edu.config.RoleName.*;

@SpringBootApplication
@DeclareRoles({GUEST, ADMIN, CLIENT, EMPLOYEE})
public class YerbaCafeTeaShopBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(YerbaCafeTeaShopBackendApplication.class, args);
	}

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
}
