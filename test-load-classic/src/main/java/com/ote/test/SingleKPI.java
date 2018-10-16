package com.ote.test;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SingleKPI implements KPI {

    private final long numOfThreads;
    private final long min;
    private final long average;
    private final long max;
    private final long total;
    private final long numOfFailures;

    @Override
    public String toString() {
        return this.serialize();
    }
}