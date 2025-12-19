package com.cloudgarden;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CloudGardenApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudGardenApplication.class, args);
    }
}
