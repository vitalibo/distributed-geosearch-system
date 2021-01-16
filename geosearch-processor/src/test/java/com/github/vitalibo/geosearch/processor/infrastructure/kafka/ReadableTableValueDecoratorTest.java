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

public class ReadableTableValueDecoratorTest extends TestTopology {

    @Test
    public void testTable() {
        TestInputTopic<String, String> inputTopic = createMockInputTopic(SerDe.String(), SerDe.String());
        TestOutputTopic<String, Integer> outputTopic = createMockOutputTopic(SerDe.String(), SerDe.Integer());
        final StreamsBuilder builder = new StreamsBuilder();
        ReadableTableValueDecorator<String, String, Integer> decorator =
            new ReadableTableValueDecorator<>(inputTopic, String::length);
        outputTopic.writeTo(decorator.table(builder).toStream());
        configure(builder);

        inputTopic.pipeInput("k1", "1");
        inputTopic.pipeInput("k2", "22");

        List<KeyValue<String, Integer>> actual = outputTopic.readKeyValuesToList();
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.size(), 2);
        Assert.assertEquals(actual.get(0).key, "k1");
        Assert.assertEquals(actual.get(0).value, Integer.valueOf(1));
        Assert.assertEquals(actual.get(1).key, "k2");
        Assert.assertEquals(actual.get(1).value, Integer.valueOf(2));
    }

}