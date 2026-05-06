package com.laventurosa.infrastructure.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.laventurosa")
public class BackendMain {
    public static void main(String[] args) {
        SpringApplication.run(BackendMain.class, args);
    }
}
