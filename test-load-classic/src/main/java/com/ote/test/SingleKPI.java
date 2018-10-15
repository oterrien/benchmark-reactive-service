package com.ote.test;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SingleKPI implements KPI {
    private final long numOfThreads, min, average, max, total, numOfFailures;

}