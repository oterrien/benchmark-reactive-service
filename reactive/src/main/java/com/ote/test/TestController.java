package com.ote.test;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    private TestService service;

    @GetMapping("/{id}")
    public Mono<String> getResult(@PathVariable("id") int id) throws Exception {
        //return Mono.just(service.call(id));
        return Mono.just(service.call(id)).publishOn(Schedulers.elastic());
    }
}
