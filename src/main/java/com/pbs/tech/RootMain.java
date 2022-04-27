package com.pbs.tech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RootMain {
    public static void main(String[] args) {
        SpringApplication.run(RootMain.class, args);
    }
}
