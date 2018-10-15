package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        List<User> users = IntStream.range(0, 100000).parallel().mapToObj(this::createUser).collect(Collectors.toList());
        userRepository.saveAll(users);
        log.warn("##### Num of elements: " + userRepository.count());
    }

    private User createUser(long index) {
        User user = new User();
        user.setIndex(index);
        user.setName("SUCCESS-" + index);
        return user;
    }
    @GetMapping("/{id}")
    public String getResult(@PathVariable("id") int id) throws Exception {
        return userRepository.findByName("SUCCESS-" + id).map(User::getName).orElse("ERROR-" + id);
    }

}