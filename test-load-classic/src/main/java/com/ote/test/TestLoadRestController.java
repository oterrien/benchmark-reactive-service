package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.IntStream;

@RestController
@Slf4j
public class TestLoadRestController {

    @Autowired
    private RestOperations restTemplate;

    @GetMapping(value = "/test/{type}/{numThreads}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String test(@PathVariable("type") Type type,
                       @PathVariable("numThreads") int numThreads) throws Exception {

        System.gc();
        TimeUnit.SECONDS.sleep(1);

        UUID uuid = UUID.randomUUID();

        log.info(String.format("# START CALL %s-------------------------------------------------------", uuid.toString()));

        Function<Integer, Status> function = getFunction(type);
        KPI kpi = executeTest(5, numThreads, type, uuid, function);
        return kpi.serialize();
    }

    private Function<Integer, Status> getFunction(Type type) {
        switch (type) {
            case synchronous:
                return this::callSynchronous;
            case reactive:
                return this::callReactive;
            default:
                throw new IllegalArgumentException("Type " + type + " is not known");
        }
    }

    private KPI executeTest(int numOfShoot, int numThreads, Type type, UUID uuid, Function<Integer, Status> function) {

        List<KPI> kpis = new ArrayList<>(numOfShoot);
        IntStream.range(0, numOfShoot).forEach(i -> kpis.add(executeTest(numThreads, type, uuid, function)));
        return new AverageKPI(kpis);
    }

    private KPI executeTest(int numThreads, Type type, UUID uuid, Function<Integer, Status> function) {

        Metrics metrics = new Metrics(numThreads, type, uuid);
        IntStream.range(0, numThreads).forEach(i -> metrics.newRequest(i, function));
        metrics.startAndWaitAll();

        long min = Math.round(metrics.getMinDuration());
        long max = Math.round(metrics.getMaxDuration());
        long average = Math.round(metrics.getAverageDuration());
        long total = Math.round(metrics.getTotalDuration());
        long numOfFailures = Math.round(metrics.getNumOfFailures());

        return new SingleKPI(numThreads, min, average, max, total, numOfFailures);
    }

    private Status callSynchronous(int index) {
        return call(index, (idx) -> restTemplate.
                getForEntity("http://localhost:8080/test/" + index, String.class).
                getBody());
    }

    private Status callReactive(int index) {
        return call(index, (idx) -> WebClient.
                create("http://localhost:8081/test/" + index).
                get().
                retrieve().
                bodyToMono(String.class).
                block());
    }

    private Status call(int index, Function<Integer, String> function) {
        Status status = new Status();
        do {
            status.incrementLoop();
            try {
                status.setResult(function.apply(index));
            } catch (Exception e) {
                status.setResult("ERROR-" + index);
            }
        } while (!status.isSuccess());
        return status;
    }
}