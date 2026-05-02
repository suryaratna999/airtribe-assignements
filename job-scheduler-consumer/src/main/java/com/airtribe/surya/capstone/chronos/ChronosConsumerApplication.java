package com.airtribe.surya.capstone.chronos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChronosConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChronosConsumerApplication.class, args);
    }
}