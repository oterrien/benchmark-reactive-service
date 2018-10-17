package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
@Slf4j
public class TestLoadRestController {

    @GetMapping(value = "/test/{type}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String test(@PathVariable("type") Type type,
                       @RequestParam("numThreads") String numThreads) {

        final UUID uuid = UUID.randomUUID();
        try {
            final Function<Integer, Status> function = type.getFunction();

            log.info("# START CALL {}-------------------------------------------------------", uuid.toString());

            return Stream.of(numThreads.split(",")).
                    mapToInt(Integer::parseInt).
                    mapToObj(i -> executeTest(2, i, type, uuid, function).serialize()).
                    collect(Collectors.joining(""));
        } finally {
            log.info("# END CALL {}-------------------------------------------------------", uuid.toString());
        }
    }

    private KPI executeTest(int numOfShoot, int numThreads, Type type, UUID uuid, Function<Integer, Status> function) {

        List<KPI> kpis = new ArrayList<>(numOfShoot);
        IntStream.range(0, numOfShoot).forEach(i -> kpis.add(executeTest(numThreads, type, uuid, function)));
        return new MinKPI(kpis);
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
}