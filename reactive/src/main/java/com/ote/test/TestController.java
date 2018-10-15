package com.ote.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.TimeUnit;

@RestController
public class TestController {

    @GetMapping("/test/reactive/{id}")
    public Mono<String> getResult(@PathVariable("id") int id) throws Exception {
        //System.out.println(id);
        return Mono.fromCallable(() -> this.call(id)).subscribeOn(Schedulers.elastic());
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
