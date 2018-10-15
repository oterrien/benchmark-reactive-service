package com.ote.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ClassicMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClassicMsApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(@Autowired UserRepository userRepository) {
        return args -> {
            for (int i = 0; i < 100000; i++) {
                UserEntity userEntity = new UserEntity();
                userEntity.setIndex(i);
                userEntity.setName("SUCCESS-" + i);
                userRepository.saveAndFlush(userEntity);
            }
        };
    }
}
