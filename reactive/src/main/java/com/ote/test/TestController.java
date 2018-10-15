package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        Flux<User> users = Flux.fromStream(IntStream.range(0, 10).mapToObj(this::createUser));
        userRepository.saveAll(users).then().publishOn(Schedulers.elastic());
        log.warn("##### Num of elements: " + userRepository.count().block());
    }

    private User createUser(long index) {
        User user = new User();
        user.setIndex(index);
        user.setName("SUCCESS-" + index);
        return user;
    }

    @GetMapping("/{id}")
    public Mono<String> getResult(@PathVariable("id") int id) throws Exception {
        return userRepository.findByName("SUCCESS-" + id).map(User::getName).defaultIfEmpty("ERROR-" + id);
    }
}
