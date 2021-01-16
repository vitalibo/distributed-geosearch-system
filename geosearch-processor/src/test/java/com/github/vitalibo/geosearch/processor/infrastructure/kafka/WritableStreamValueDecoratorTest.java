package com.github.vitalibo.geosearch.processor.infrastructure.kafka;

import com.github.vitalibo.geosearch.processor.TestInputTopic;
import com.github.vitalibo.geosearch.processor.TestOutputTopic;
import com.github.vitalibo.geosearch.processor.TestTopology;
import com.github.vitalibo.geosearch.processor.core.util.SerDe;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class WritableStreamValueDecoratorTest extends TestTopology {

    @Test
    public void testWriteTo() {
        TestInputTopic<String, Integer> inputTopic = createMockInputTopic(SerDe.String(), SerDe.Integer());
        TestOutputTopic<String, Long> outputTopic = createMockOutputTopic(SerDe.String(), SerDe.Long());
        final StreamsBuilder builder = new StreamsBuilder();
        WritableStreamValueDecorator<String, Long, Integer> decorator =
            new WritableStreamValueDecorator<>(outputTopic, v -> Arrays.asList((long) v / 2L, (long) v, v * 2L));
        decorator.writeTo(inputTopic.stream(builder));
        configure(builder);

        inputTopic.pipeInput("foo", 10);

        List<KeyValue<String, Long>> actual = outputTopic.readKeyValuesToList();
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.size(), 3);
        Assert.assertEquals(actual.get(0).key, "foo");
        Assert.assertEquals(actual.get(0).value, Long.valueOf(5));
        Assert.assertEquals(actual.get(1).key, "foo");
        Assert.assertEquals(actual.get(1).value, Long.valueOf(10));
        Assert.assertEquals(actual.get(2).key, "foo");
        Assert.assertEquals(actual.get(2).value, Long.valueOf(20));
    }

}
