package Middleware;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@EnableAsync
@RestController
public class MiddlewareApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiddlewareApplication.class, args);
        initCSVFile();
    }

    private static void initCSVFile(){
        // Create the directory
        File directory = new File("/middleware_data/");
        if (!directory.exists()) {
            directory.mkdir();
        }
        // Create the file
        File file = new File(directory, "devices.csv");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
