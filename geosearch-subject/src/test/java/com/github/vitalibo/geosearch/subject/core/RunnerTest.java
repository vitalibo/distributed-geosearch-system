package com.github.vitalibo.geosearch.subject.core;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;

public class RunnerTest {

    @Mock
    private Source<String> mockSource;
    @Mock
    private EventDrivenSource<String> mockEventDrivenSource;
    @Mock
    private Channel<String> mockChannel;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
    }

    @Test
    public void testProcess() {
        Runner<String> runner = new Runner<>(mockSource, mockChannel);
        Mockito.when(mockSource.iterator()).thenReturn(Arrays.asList("foo", "bar").iterator());

        runner.process();

        Mockito.verify(mockSource).iterator();
        Mockito.verify(mockChannel).send("foo");
        Mockito.verify(mockChannel).send("bar");
        Mockito.verify(mockSource).close();
        Mockito.verify(mockChannel).close();
    }

    @Test
    public void testProcessEventDrivenSource() {
        Runner<String> runner = new Runner<>(mockEventDrivenSource, mockChannel);
        Mockito.when(mockEventDrivenSource.iterator()).thenReturn(Arrays.asList("foo", "bar").iterator());

        runner.process();

        Mockito.verify(mockEventDrivenSource).iterator();
        Mockito.verify(mockEventDrivenSource).start();
        Mockito.verify(mockChannel).send("foo");
        Mockito.verify(mockChannel).send("bar");
        Mockito.verify(mockEventDrivenSource).close();
        Mockito.verify(mockChannel).close();
    }

}