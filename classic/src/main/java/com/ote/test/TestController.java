package com.ote.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private UserRepository repository;

    @GetMapping("/test/sync/{id}")
    public String getResult(@PathVariable("id") int id) throws Exception {
        return repository.findByIndex(id).map(UserEntity::getName).orElse("ERROR-" + id);
    }

}