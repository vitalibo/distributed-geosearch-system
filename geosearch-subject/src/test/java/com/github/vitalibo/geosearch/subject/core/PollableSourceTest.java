package com.github.vitalibo.geosearch.subject.core;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Optional;

public class PollableSourceTest {

    @Test
    public void testNext() {
        TestPollableSource spySource = Mockito.spy(new TestPollableSource(10));
        Mockito.when(spySource.process()).thenReturn(Optional.empty(), Optional.empty())
            .thenReturn(Optional.of("foo")).thenReturn(Optional.empty());

        String actual = spySource.next();

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, "foo");
        Mockito.verify(spySource, Mockito.times(3)).process();
    }

    private static class TestPollableSource extends PollableSource<String> {

        public TestPollableSource(int backOffSleepIntervalMillis) {
            super(backOffSleepIntervalMillis);
        }

        @Override
        public Optional<String> process() {
            return Optional.empty();
        }
    }
}
