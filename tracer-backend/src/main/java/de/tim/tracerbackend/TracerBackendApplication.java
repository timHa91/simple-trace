package de.tim.tracerbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TracerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TracerBackendApplication.class, args);
    }

}
