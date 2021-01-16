package com.github.vitalibo.geosearch.subject.core;

import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Iterator;

public class SourceTest {

    @Mock
    private Runtime mockRuntime;
    @Captor
    private ArgumentCaptor<Thread> captorThread;

    private TestSource source;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        source = new TestSource(mockRuntime);
    }

    @Test
    public void testIterator() {
        Iterator<String> actual = source.iterator();

        Assert.assertNotNull(actual);
        Assert.assertSame(actual, source);
    }

    @Test
    public void testHasNext() {
        boolean actual = source.hasNext();

        Assert.assertTrue(actual);
    }

    @Test
    public void testNext() {
        String actual = source.next();

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, "foo");
    }

    @Test
    public void testAddShutdownHook() {
        Mockito.verify(mockRuntime).addShutdownHook(captorThread.capture());
        Thread actual = captorThread.getValue();

        Assert.assertTrue(source.hasNext());
        actual.run();
        Assert.assertFalse(source.hasNext());
    }

    private static class TestSource extends Source<String> {

        public TestSource(Runtime runtime) {
            super(runtime);
        }

        @Override
        public String next() {
            return "foo";
        }

    }

}