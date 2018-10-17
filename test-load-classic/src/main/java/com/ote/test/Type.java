package com.ote.test;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Function;

@Slf4j
public enum Type {
    synchronous(Type::callSynchronous), reactive(Type::callReactive);

    @Getter
    private Function<Integer, Status> function;

    Type(Function<Integer, Status> function){
        this.function = function;
    }

    private static Status callSynchronous(int index) {
        /*return call(index, (idx) -> restTemplate.
                getForEntity("http://localhost:8080/test/" + index, String.class).
                getBody());*/
        return call(index, (idx) -> WebClient.
                create("http://localhost:8080/test/" + index).
                get().
                retrieve().
                bodyToMono(String.class).
                block());
    }

    private static Status callReactive(int index) {
        return call(index, (idx) -> WebClient.
                create("http://localhost:8081/test/" + index).
                get().
                retrieve().
                bodyToMono(String.class).
                block());
    }

    private static Status call(int index, Function<Integer, String> function) {
        Status status = new Status();
        do {
            status.incrementLoop();
            try {
                status.setResult(function.apply(index));
            } catch (Exception e) {
                log.error("An error occured for index {} : {}", index, e.getMessage());
                status.setResult("ERROR-" + index);
            }
        } while (!status.isSuccess());
        return status;
    }
}