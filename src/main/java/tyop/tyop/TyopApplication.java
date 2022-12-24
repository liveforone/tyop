package tyop.tyop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TyopApplication {

	public static void main(String[] args) {
		SpringApplication.run(TyopApplication.class, args);
	}

}
