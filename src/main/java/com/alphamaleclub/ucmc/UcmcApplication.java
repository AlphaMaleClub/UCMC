package com.alphamaleclub.ucmc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@ConfigurationPropertiesScan
public class UcmcApplication {

    public static void main(String[] args) {
        SpringApplication.run(UcmcApplication.class, args);
    }


}