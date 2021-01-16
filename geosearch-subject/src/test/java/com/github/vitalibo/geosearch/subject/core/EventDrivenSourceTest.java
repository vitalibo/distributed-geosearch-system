package com.github.vitalibo.geosearch.subject.core;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class EventDrivenSourceTest {

    @Mock
    private BlockingQueue<String> mockBlockingQueue;
    @Mock
    private ExecutorService mockExecutorService;
    @Mock
    private Runnable mockRunnable;

    private EventDrivenSource<String> source;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        source = new TestEventDrivenSource(mockBlockingQueue, mockExecutorService);
    }

    @Test
    public void testSubmit() {
        source.submit(mockRunnable);

        Mockito.verify(mockExecutorService).submit(mockRunnable);
    }

    @Test
    public void testNext() throws InterruptedException {
        Mockito.when(mockBlockingQueue.take()).thenReturn("foo");

        String actual = source.next();

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, "foo");
    }

    @Test
    public void testProcess() throws InterruptedException {
        source.process("foo");

        Mockito.verify(mockBlockingQueue).put("foo");
    }

    public static class TestEventDrivenSource extends EventDrivenSource<String> {

        public TestEventDrivenSource(BlockingQueue<String> queue, ExecutorService executor) {
            super(queue, executor);
        }

        @Override
        public void start() {
        }

    }

}
