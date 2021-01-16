package com.github.vitalibo.geosearch.processor.core;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;

import static org.apache.kafka.streams.KafkaStreams.State.NOT_RUNNING;

@RequiredArgsConstructor
public class KafkaStreams implements AutoCloseable {

    @Delegate
    private final org.apache.kafka.streams.KafkaStreams streams;
    private final Runtime runtime;

    public KafkaStreams(org.apache.kafka.streams.KafkaStreams streams) {
        this(streams, Runtime.getRuntime());
    }

    @SneakyThrows
    public void awaitTermination() {
        runtime.addShutdownHook(new Thread(streams::close));

        while (!NOT_RUNNING.equals(streams.state())) {
            Thread.sleep(1000);
        }
    }

}
