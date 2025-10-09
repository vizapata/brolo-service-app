package co.ateunti.brolo.target;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TargetApp {

    public static void main(String[] args) {
        SpringApplication.run(TargetApp.class, args);
    }
}
