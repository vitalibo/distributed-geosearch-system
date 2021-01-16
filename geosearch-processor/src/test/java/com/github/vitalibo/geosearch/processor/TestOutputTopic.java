package com.github.vitalibo.geosearch.processor;

import com.github.vitalibo.geosearch.processor.core.Topic;
import com.github.vitalibo.geosearch.processor.core.Writable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.apache.kafka.streams.TopologyTestDriver;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class TestOutputTopic<K, V> implements Writable.Stream<K, V> {

    @Delegate(types = Writable.Stream.class)
    private final Topic<K, V> topic;
    private final Supplier<TopologyTestDriver> supplier;

    @Delegate
    @Getter(lazy = true)
    private final org.apache.kafka.streams.TestOutputTopic<K, V> testOutputTopic =
        createInternalTestOutputTopic(supplier.get(), topic);

    private static <K, V> org.apache.kafka.streams.TestOutputTopic<K, V> createInternalTestOutputTopic(TopologyTestDriver driver, Topic<K, V> topic) {
        return driver.createOutputTopic(topic.getName(), topic.getKeySerDe().deserializer(), topic.getValueSerDe().deserializer());
    }

}
