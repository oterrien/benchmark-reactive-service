package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @GetMapping("/{id}")
    public String getResult(@PathVariable("id") int id) throws Exception {
        return call(id);
    }

    private String call(int id) {
        try {
            TimeUnit.MILLISECONDS.sleep(10);
            return "SUCCESS-" + id;
        } catch (Exception e) {
            return "ERROR-" + id;
        }
    }

}