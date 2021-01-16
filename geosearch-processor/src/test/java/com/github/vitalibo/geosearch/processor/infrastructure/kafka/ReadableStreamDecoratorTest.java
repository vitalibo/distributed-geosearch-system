package com.github.vitalibo.geosearch.processor.infrastructure.kafka;

import com.github.vitalibo.geosearch.processor.TestInputTopic;
import com.github.vitalibo.geosearch.processor.TestOutputTopic;
import com.github.vitalibo.geosearch.processor.TestTopology;
import com.github.vitalibo.geosearch.processor.core.util.SerDe;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReadableStreamDecoratorTest extends TestTopology {

    @Test
    public void testStream() {
        TestInputTopic<String, Integer> inputTopic = createMockInputTopic(SerDe.String(), SerDe.Integer());
        TestOutputTopic<String, String> outputTopic = createMockOutputTopic(SerDe.String(), SerDe.String());
        final StreamsBuilder builder = new StreamsBuilder();
        ReadableStreamDecorator<String, Integer, String, String> decorator =
            new ReadableStreamDecorator<>(inputTopic, (k, v) -> IntStream.range(0, v)
                .mapToObj(i -> new KeyValue<>(k + (v - i - 1), k + i))
                .collect(Collectors.toList()));
        outputTopic.writeTo(decorator.stream(builder));
        configure(builder);

        inputTopic.pipeInput("foo", 3);

        List<KeyValue<String, String>> actual = outputTopic.readKeyValuesToList();
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.size(), 3);
        Assert.assertEquals(actual.get(0).key, "foo2");
        Assert.assertEquals(actual.get(0).value, "foo0");
        Assert.assertEquals(actual.get(1).key, "foo1");
        Assert.assertEquals(actual.get(1).value, "foo1");
        Assert.assertEquals(actual.get(2).key, "foo0");
        Assert.assertEquals(actual.get(2).value, "foo2");
    }

}
