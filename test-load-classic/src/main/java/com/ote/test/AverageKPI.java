package com.ote.test;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AverageKPI implements KPI {
    private final List<KPI> kpis;

    @Override
    public long getNumOfThreads() {
        return kpis.parallelStream().findAny().map(KPI::getNumOfThreads).orElse(0L);
    }

    @Override
    public long getMin() {
        return Math.round(kpis.parallelStream().mapToLong(KPI::getMin).average().orElse(-1));
    }

    @Override
    public long getMax() {
        return Math.round(kpis.parallelStream().mapToLong(KPI::getMax).average().orElse(-1));
    }

    @Override
    public long getAverage() {
        return Math.round(kpis.parallelStream().mapToLong(KPI::getAverage).average().orElse(-1));
    }

    @Override
    public long getTotal() {
        return Math.round(kpis.parallelStream().mapToLong(KPI::getTotal).average().orElse(-1));
    }

    @Override
    public long getNumOfFailures() {
        return Math.round(kpis.parallelStream().mapToLong(KPI::getNumOfFailures).average().orElse(-1));
    }

    @Override
    public String toString(){
        return this.serialize();
    }
}