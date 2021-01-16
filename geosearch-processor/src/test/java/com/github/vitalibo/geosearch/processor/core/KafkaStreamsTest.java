package com.github.vitalibo.geosearch.processor.core;

import org.apache.kafka.streams.KafkaStreams.State;
import org.mockito.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class KafkaStreamsTest {

    @Mock
    private org.apache.kafka.streams.KafkaStreams mockKafkaStreams;
    @Mock
    private Runtime mockRuntime;
    @Captor
    private ArgumentCaptor<Thread> captorThread;

    private KafkaStreams spyKafkaStreams;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        spyKafkaStreams = Mockito.spy(new KafkaStreams(mockKafkaStreams, mockRuntime));
    }

    @Test
    public void testAwaitTerminationNotRunning() {
        Mockito.when(mockKafkaStreams.state())
            .thenReturn(State.CREATED, State.RUNNING, State.RUNNING, State.NOT_RUNNING, State.ERROR);

        spyKafkaStreams.awaitTermination();

        Mockito.verify(mockKafkaStreams, Mockito.times(4)).state();
        Mockito.verify(mockKafkaStreams, Mockito.never()).close();
    }

    @Test
    public void testAwaitTerminationError() {
        Mockito.when(mockKafkaStreams.state())
            .thenReturn(State.CREATED, State.RUNNING, State.RUNNING, State.ERROR, State.NOT_RUNNING);

        spyKafkaStreams.awaitTermination();

        Mockito.verify(mockKafkaStreams, Mockito.times(4)).state();
        Mockito.verify(mockKafkaStreams, Mockito.never()).close();
    }

    @Test
    public void testShutdownHook() {
        Mockito.when(mockKafkaStreams.state()).thenReturn(State.NOT_RUNNING);

        spyKafkaStreams.awaitTermination();

        Mockito.verify(mockRuntime).addShutdownHook(captorThread.capture());
        Thread thread = captorThread.getValue();
        thread.run();
        Mockito.verify(mockKafkaStreams).close();
    }

    @Test
    public void testStart() {
        Mockito.doNothing().when(mockKafkaStreams).start();

        spyKafkaStreams.start();

        Mockito.verify(mockKafkaStreams).start();
    }

}
