package ApplicationGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@RestController
public class ApplicationGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationGatewayApplication.class, args);
	}

}
