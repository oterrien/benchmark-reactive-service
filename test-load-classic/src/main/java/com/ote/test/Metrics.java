package com.ote.test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class Metrics {

    @Getter
    private final int numThreads;

    @Getter
    private final List<Metric> metrics;

    private final UUID uuid;

    @Getter
    private final Type type;

    @JsonIgnore
    private final ExecutorService executorService;

    private long totalDuration;

    public Metrics(int numThreads, Type type,  UUID uuid) {
        this.numThreads = numThreads;
        this.type = type;
        this.uuid = uuid;
        this.metrics = new ArrayList<>(numThreads);
        this.executorService = Executors.newFixedThreadPool(numThreads);
    }

    public void newRequest(int threadNum, Function<Integer, String> function) {
        Metric metric = new Metric(threadNum, this.uuid, this.type, function, executorService);
        metrics.add(metric);
    }

    public void startAll() {
        long beginTime = System.currentTimeMillis();
        CompletableFuture.allOf(metrics.parallelStream().map(Metric::start).toArray(CompletableFuture[]::new)).join();
        this.totalDuration = System.currentTimeMillis() - beginTime;
        executorService.shutdownNow();
    }

    public long getTotalDuration() {
        return this.totalDuration;
    }

    public double getMinDuration() {
        return this.metrics.stream().filter(p -> p.getResult().contains("SUCCESS")).mapToLong(Metric::getDuration).min().orElse(-1);
    }

    public double getMaxDuration() {
        return this.metrics.stream().filter(p -> p.getResult().contains("SUCCESS")).mapToLong(Metric::getDuration).max().orElse(-1);
    }

    public double getAverageDuration() {
        return this.metrics.stream().filter(p -> p.getResult().contains("SUCCESS")).mapToLong(Metric::getDuration).average().orElse(-1);
    }

    public long getNumOfSuccess() {
        return this.metrics.stream().filter(p -> p.getResult().contains("SUCCESS")).count();
    }

    public long getNumOfError() {
        return this.metrics.stream().filter(p -> !p.getResult().contains("SUCCESS")).count();
    }
}