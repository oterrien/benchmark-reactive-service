package com.ote.test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

@Data
@RequiredArgsConstructor
public class Metric {

    private final int index;

    private final UUID uuid;

    private final Type type;

    @JsonIgnore
    private Function<Integer, Status> function;

    @JsonIgnore
    private ExecutorService executorService;

    private Status result;

    @JsonIgnore
    private long beginTime;

    @JsonIgnore
    private long endTime;

    public Metric(int index, UUID uuid, Type type, Function<Integer, Status> function, ExecutorService executorService) {
        this(index, uuid, type);
        this.function = function;
        this.executorService = executorService;
    }

    public CompletableFuture<Void> start() {
        try {
            this.beginTime = System.currentTimeMillis();
            return CompletableFuture.supplyAsync(() -> function.apply(index), executorService).thenApplyAsync(this::end, executorService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Void end(Status result) {
        this.endTime = System.currentTimeMillis();
        this.result = result;
        return null;
    }

    public long getDuration() {
        return this.endTime - this.beginTime;
    }

}