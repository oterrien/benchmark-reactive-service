package com.ote.test;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;
import java.util.function.Function;

@RestController
@Log
public class TestLoadRestController {

    @Autowired
    private MetricElasticsearchRespository repository;

    @Autowired
    private RestOperations restTemplate;

    @GetMapping("/test/{type}/{numThreads}")
    public Metrics test(@PathVariable("type") Type type,
                        @PathVariable("numThreads") int numThreads) {

        /**
         * Sauvegarder les résultats sur elasticsearch
         */
        UUID uuid = UUID.randomUUID();

        log.info(String.format("# START CALL %s-------------------------------------------------------", uuid.toString()));

        Function<Integer, String> function = null;
        switch (type) {
            case synchronous:
                function = this::callSynchronous;
                break;
            case asynchronous:
                function = this::callAsynchronous;
                break;
            case reactive:
                function = this::callReactive;
                break;
        }


        Metrics metrics = new Metrics(numThreads, type, uuid);
        for (int i = 0; i < numThreads; i++) {
            metrics.newRequest(i, function);
        }
        metrics.startAll();

        log.info("# END CALL -------------------------------------------------------");
        log.info("# START SAVE METRIC -------------------------------------------------------");

        metrics.getMetrics().parallelStream().
                map(p -> new MetricDocument(p.getIndex(), p.getUuid(), p.getType(), (p.getEndTime() - p.getBeginTime()), p.getResult())).
                forEach(p -> repository.save(p));

        log.info("# END SAVE METRIC -------------------------------------------------------");

        return metrics;
    }


    private String callSynchronous(int index) {
        try {
            return restTemplate.getForEntity("http://localhost:8080/test/sync/" + index, String.class).getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    private String callAsynchronous(int index) {
        try {
            return restTemplate.getForEntity("http://localhost:8080/test/async/" + index, String.class).getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    private String callReactive(int index) {
        try {
            return WebClient.
                    create("http://localhost:8081/test/reactive/" + index).
                    get().
                    retrieve().
                    bodyToMono(String.class).
                    block();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
}