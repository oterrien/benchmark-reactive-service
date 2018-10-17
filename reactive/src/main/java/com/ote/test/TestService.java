package com.ote.test;

import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TestService {

    public String call(int id) {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
            return "SUCCESS-" + id;
        } catch (Exception e) {
            return "ERROR-" + id;
        }
    }

}
