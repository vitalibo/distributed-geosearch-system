package com.github.vitalibo.geosearch.processor;

import com.github.vitalibo.geosearch.processor.core.KafkaStreams;
import com.github.vitalibo.geosearch.processor.infrastructure.Factory;
import org.apache.kafka.streams.Topology;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ApplicationTest {

    @Mock
    private Factory mockFactory;
    @Mock
    private Topology mockTopology;
    @Mock
    private KafkaStreams mockKafkaStreams;

    private Application application;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        Mockito.when(mockFactory.createGeoSearchTopology()).thenReturn(mockTopology);
        Mockito.when(mockFactory.createKafkaStream(Mockito.any())).thenReturn(mockKafkaStreams);
        application = new Application(mockFactory);
    }

    @Test
    public void testGeoSearchTopology() {
        application.run(new String[]{"geosearch"});

        Mockito.verify(mockFactory).createGeoSearchTopology();
        Mockito.verify(mockFactory).createKafkaStream(mockTopology);
        Mockito.verify(mockKafkaStreams).cleanUp();
        Mockito.verify(mockKafkaStreams).start();
        Mockito.verify(mockKafkaStreams).awaitTermination();
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "Unknown topology name")
    public void testUnknownTopology() {
        application.run(new String[]{"foo"});
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testFailStreams() {
        Mockito.doThrow(RuntimeException.class).when(mockKafkaStreams).start();

        application.run(new String[]{"geosearch"});
    }

}
