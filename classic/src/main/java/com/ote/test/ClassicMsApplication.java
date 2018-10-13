package com.ote.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableAsync
public class ClassicMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClassicMsApplication.class, args);
    }
}

@RestController
class TestController {

    @PostMapping
    public void start() {
        System.out.println("-------------------------------------------------------");
    }

    @GetMapping("/test/sync/{id}")
    public String getResult(@PathVariable("id") int id) throws Exception {
      //  System.out.println(id);
        return call(id);
    }

    @GetMapping(value = "/test/async/{id}")
    @Async
    public CompletableFuture<String> getResultCallable(@PathVariable("id") int id) throws Exception {
        //System.out.println(id);
        return CompletableFuture.supplyAsync(() -> this.call(id));
    }

    private String call(int id) {
        try {
            TimeUnit.SECONDS.sleep(1);
            return "SUCCESS-" + id;
        } catch (Exception e) {
            return "ERROR-" + id;
        }
    }

}
