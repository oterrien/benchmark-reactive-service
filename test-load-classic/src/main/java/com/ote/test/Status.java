package com.ote.test;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class Status {

    @Getter
    @Setter
    private String result;

    @Getter
    private int numFailures = -1;

    public boolean isSuccess() {
        return StringUtils.contains(result, "SUCCESS");
    }

    public void incrementLoop() {
        this.numFailures++;
    }
}
