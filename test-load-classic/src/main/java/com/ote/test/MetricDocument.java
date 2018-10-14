package com.ote.test;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

@Data
@RequiredArgsConstructor
@Document(indexName="metrics", type="metric")
public class MetricDocument {

    @Id
    private final int index;

    private final UUID uuid;

    private final Type type;

    private final long duration;

    private final String result;
}
