package com.groupeisi.company;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AchatEnLigneMsApplication {
    public static void main(String[] args) {
        SpringApplication.run(AchatEnLigneMsApplication.class, args);
    }
}
