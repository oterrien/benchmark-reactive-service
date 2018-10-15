package com.ote.test;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@Log
public class TestLoadRestController {

    @Autowired
    private MetricElasticsearchRespository repository;

    @Autowired
    private RestOperations restTemplate;

    @GetMapping("/test/{type}/{numThreads}")
    public String test(@PathVariable("type") Type type,
                       @PathVariable("numThreads") int numThreads) throws Exception {


        System.gc();
        TimeUnit.SECONDS.sleep(1);

        UUID uuid = UUID.randomUUID();

        log.info(String.format("# START CALL %s-------------------------------------------------------", uuid.toString()));

        Function<Integer, String> function = null;
        switch (type) {
            case synchronous:
                function = this::callSynchronous;
                break;
            case reactive:
                function = this::callReactive;
                break;
        }

        KPI kpi = executeTest(4, numThreads, type, uuid, function);

       return String.format("%d;%d;%d;%d", numThreads, kpi.getAverage(), kpi.getMax(), kpi.getTotal());
    }

    private KPI executeTest(int numOfShoot, int numThreads, Type type, UUID uuid, Function<Integer, String> function) {

        List<KPI> kpis = new ArrayList<>(numOfShoot);
        for (int i = 0; i < numOfShoot; i++) {
            kpis.add(executeTest(numThreads, type, uuid, function));
        }

        return new AverageKPI(kpis);
    }

    private KPI executeTest(int numThreads, Type type, UUID uuid, Function<Integer, String> function) {
        Metrics metrics = new Metrics(numThreads, type, uuid);
        for (int i = 0; i < numThreads; i++) {
            metrics.newRequest(i, function);
        }
        metrics.startAndWaitAll();

        long min = Math.round(metrics.getMinDuration());
        long max = Math.round(metrics.getMaxDuration());
        long average = Math.round(metrics.getAverageDuration());
        long total = Math.round(metrics.getTotalDuration());

        return new SingleKPI(min, max, average, total);
    }

    private interface KPI {
        long getMin();
        long getMax();
        long getAverage();
        long getTotal();
    }

    @RequiredArgsConstructor
    private static class AverageKPI implements KPI {
        private final List<KPI> kpis;

        @Override
        public long getMin() {
            return Math.round(kpis.stream().mapToLong(KPI::getMin).average().orElse(-1));
        }

        @Override
        public long getMax() {
            return Math.round(kpis.stream().mapToLong(KPI::getMax).average().orElse(-1));
        }

        @Override
        public long getAverage() {
            return Math.round(kpis.stream().mapToLong(KPI::getAverage).average().orElse(-1));
        }

        @Override
        public long getTotal() {
            return Math.round(kpis.stream().mapToLong(KPI::getTotal).average().orElse(-1));
        }
    }

    @Getter
    @RequiredArgsConstructor
    private static class SingleKPI implements KPI {
        private final long min, max, average, total;
    }

    private String callSynchronous(int index) {
        String result;
        do {
            try {
                result = restTemplate.getForEntity("http://localhost:8080/test/sync/" + index, String.class).getBody();
            } catch (Exception e) {
                result = "ERROR-" + index;
            }
        } while (StringUtils.contains(result, "ERROR"));
        return result;
    }

    private String callReactive(int index) {
        String result;
        do {
            try {
                result = WebClient.
                        create("http://localhost:8081/test/reactive/" + index).
                        get().
                        retrieve().
                        bodyToMono(String.class).
                        block();
            } catch (Exception e) {
                //e.printStackTrace();
                result = "ERROR-" + index;
            }
        } while (StringUtils.contains(result, "ERROR"));
        return result;
    }
}