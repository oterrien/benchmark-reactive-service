package com.ote.test;

public interface KPI {

    long getNumOfThreads();

    long getMin();

    long getMax();

    long getAverage();

    long getTotal();

    long getNumOfFailures();

    default String serialize(){
        return String.format("%d;%d;%d;%d;%d;%d\n", getNumOfThreads(), getMin(), getAverage(), getMax(), getTotal(), getNumOfFailures());
    }
}


