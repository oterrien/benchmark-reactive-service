package com.ote.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class ReactiveMsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReactiveMsApplication.class, args);
    }
}

@RestController
class TestController {

    @GetMapping("/test/reactive/{id}")
    public Mono<String> getResult(@PathVariable("id") int id) throws Exception {
        //System.out.println(id);
        return Mono.fromCallable(() -> this.call(id)).subscribeOn(Schedulers.elastic());
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
