package com.ote.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SpringBootAppRunner {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAppRunner.class, args);
    }

    @Bean
    public RestOperations restTemplate() {
        return new RestTemplate();
    }
}


