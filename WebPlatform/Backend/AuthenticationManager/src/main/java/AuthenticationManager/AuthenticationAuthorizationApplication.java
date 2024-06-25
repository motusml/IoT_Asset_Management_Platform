package AuthenticationManager;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class AuthenticationAuthorizationApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationAuthorizationApplication.class, args);
	}

}

