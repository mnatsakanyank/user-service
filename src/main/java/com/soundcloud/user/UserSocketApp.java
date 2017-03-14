package com.soundcloud.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.soundcloud.user"})
@EnableConfigurationProperties
@SpringBootApplication
public class UserSocketApp {


    public static void main(String[] args) {
        SpringApplication.run(UserSocketApp.class, args);
    }

}
