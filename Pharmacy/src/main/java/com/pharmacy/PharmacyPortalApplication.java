package com.pharmacy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {
    "com.pharmacy",
    "com.pharmacy.dao",
    "com.pharmacy.service",
    "com.pharmacy.controller",
    "com.pharmacy.config"
})
public class PharmacyPortalApplication {
    public static void main(String[] args) {
        SpringApplication.run(PharmacyPortalApplication.class, args);
    }
}