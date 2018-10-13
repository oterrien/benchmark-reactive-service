package com.ote.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootApplication
public class TestLoadReactive {

    public static void main(String[] args) {
        SpringApplication.run(TestLoadReactive.class, args);
    }
}

@RestController
class TestLoadRestController {


    @GetMapping("/test/{type}/{numThreads}")
    public Metrics test(@PathVariable("type") Type type,
                        @PathVariable("numThreads") int numThreads) {

        /**
         * Sauvegarder les résultats sur elasticsearch
         */

        System.out.println("-------------------------------------------------------");

        Function<Integer, String> function = null;
        switch (type) {
            case reactive:
                function = this::callReactive;
                break;
        }

        Metrics metrics = new Metrics(numThreads);
        for (int i = 0; i < numThreads; i++) {
            metrics.newRequest(i, function);
        }
        metrics.startAll();
        return metrics;
    }

    enum Type {
        reactive
    }

    private String callReactive(int index) {
        try {
            //System.out.println("Call " + index);
            return WebClient.
                    create("http://localhost:8081/test/reactive/" + index).
                    get().
                    retrieve().
                    bodyToMono(String.class).
                    block();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        } finally {
           // System.out.println("End " + index);
        }
    }
}

class Metrics {

    private final List<Metric> metrics;

    private ExecutorService executorService;

    private long totalDuration;

    public Metrics(int numThreads) {
        this.metrics = new ArrayList<>(numThreads);
        executorService = Executors.newFixedThreadPool(numThreads);
    }

    public void newRequest(int index, Function<Integer, String> function) {
        Metric metric = new Metric(index, function, executorService);
        metrics.add(metric);
    }

    public void startAll() {
        long beginTime = System.currentTimeMillis();
        List<CompletableFuture> futures = metrics.parallelStream().map(p -> p.start()).collect(Collectors.toList());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        this.totalDuration = System.currentTimeMillis() - beginTime;
        executorService.shutdownNow();
    }

    public long getTotalDuration() {
        return this.totalDuration;
    }

    public double getMinDuration() {
        return metrics.stream().filter(p -> p.getResult().contains("SUCCESS")).mapToLong(p -> p.getDuration()).min().orElse(-1);
    }

    public double getMaxDuration() {
        return metrics.stream().filter(p -> p.getResult().contains("SUCCESS")).mapToLong(p -> p.getDuration()).max().orElse(-1);
    }

    public double getAverageDuration() {
        return metrics.stream().filter(p -> p.getResult().contains("SUCCESS")).mapToLong(p -> p.getDuration()).average().orElse(-1);
    }

    public int getNumThreads() {
        return this.metrics.size();
    }

    public long getNumOfSuccess() {
        return this.metrics.stream().filter(p -> p.getResult().contains("SUCCESS")).count();
    }

    public long getNumOfError() {
        return this.metrics.stream().filter(p -> !p.getResult().contains("SUCCESS")).count();
    }
}

class Metric {

    private final int index;
    private final Function<Integer, String> function;
    private ExecutorService executorService;

    private String result;

    private long beginTime;
    private long endTime;

    public Metric(int index, Function<Integer, String> function, ExecutorService executorService) {
        this.index = index;
        this.function = function;
        this.executorService = executorService;
    }

    public CompletableFuture<Void> start() {
        try {
            this.beginTime = System.currentTimeMillis();
            return CompletableFuture.supplyAsync(() -> function.apply(index), executorService).thenApplyAsync(this::end, executorService);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Void end(String result) {
       // System.out.println(index + " - " + result);
        this.endTime = System.currentTimeMillis();
        this.result = result;
        return null;
    }

    public long getDuration() {
        return this.endTime - this.beginTime;
    }

    public int getIndex() {
        return this.index;
    }

    public String getResult() {
        return this.result;
    }

}