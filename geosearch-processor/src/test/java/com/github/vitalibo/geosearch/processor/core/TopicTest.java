package com.github.vitalibo.geosearch.processor.core;

import com.github.vitalibo.geosearch.processor.core.util.SerDe;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("unchecked")
public class TopicTest {

    @Mock
    private StreamsBuilder mockStreamsBuilder;
    @Mock
    private KStream<String, Integer> mockKStream;
    @Mock
    private KTable<String, Integer> mockKTable;
    @Mock
    private GlobalKTable<String, Integer> mockGlobalKTable;

    private Topic<String, Integer> topic;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        topic = new Topic<>("foo", SerDe.String(), SerDe.Integer());
    }

    @Test
    public void testStream() {
        Mockito.when(mockStreamsBuilder.stream(Mockito.anyString(), Mockito.any(Consumed.class))).thenReturn(mockKStream);

        KStream<String, Integer> actual = topic.stream(mockStreamsBuilder);

        Assert.assertNotNull(actual);
        Assert.assertSame(actual, mockKStream);
        Mockito.verify(mockStreamsBuilder).stream(Mockito.eq("foo"), Mockito.any(Consumed.class));
    }

    @Test
    public void testWriteTo() {
        topic.writeTo(mockKStream);

        Mockito.verify(mockKStream).to(Mockito.eq("foo"), Mockito.any());
    }

    @Test
    public void testTable() {
        Mockito.when(mockStreamsBuilder.table(Mockito.anyString(), Mockito.any(Materialized.class))).thenReturn(mockKTable);

        KTable<String, Integer> actual = topic.table(mockStreamsBuilder);

        Assert.assertNotNull(actual);
        Assert.assertSame(actual, mockKTable);
        Mockito.verify(mockStreamsBuilder).table(Mockito.eq("foo"), Mockito.any(Materialized.class));
    }

    @Test
    public void testGlobalTable() {
        Mockito.when(mockStreamsBuilder.globalTable(Mockito.anyString(), Mockito.any(Materialized.class)))
            .thenReturn(mockGlobalKTable);

        GlobalKTable<String, Integer> actual = topic.globalTable(mockStreamsBuilder);

        Assert.assertNotNull(actual);
        Assert.assertSame(actual, mockGlobalKTable);
        Mockito.verify(mockStreamsBuilder).globalTable(Mockito.eq("foo"), Mockito.any(Materialized.class));
    }

}
