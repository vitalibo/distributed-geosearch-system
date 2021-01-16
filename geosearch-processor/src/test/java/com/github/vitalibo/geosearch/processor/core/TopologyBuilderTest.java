package com.github.vitalibo.geosearch.processor.core;

import org.apache.kafka.streams.kstream.KStream;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TopologyBuilderTest {

    @Mock
    private Topic<String, String> mockTopic;
    @Mock
    private KStream<String, String> mockKStream;

    private TopologyBuilder spyTopologyBuilder;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        spyTopologyBuilder = Mockito.spy(new TestTopology());
    }

    @Test
    public void testStream() {
        spyTopologyBuilder.stream(mockTopic);

        Mockito.verify(mockTopic).stream(spyTopologyBuilder);
    }

    @Test
    public void testWriteTo() {
        spyTopologyBuilder.writeTo(mockTopic, mockKStream);

        Mockito.verify(mockTopic).writeTo(mockKStream);
    }

    @Test
    public void testTable() {
        spyTopologyBuilder.table(mockTopic);

        Mockito.verify(mockTopic).table(spyTopologyBuilder);
    }

    @Test
    public void testGlobalTable() {
        spyTopologyBuilder.globalTable(mockTopic);

        Mockito.verify(mockTopic).globalTable(spyTopologyBuilder);
    }

    @Test
    public void testBuild() {
        spyTopologyBuilder.build();

        Mockito.verify(spyTopologyBuilder).defineTopology();
    }

    private static class TestTopology extends TopologyBuilder {

        @Override
        public void defineTopology() {
        }

    }

}
