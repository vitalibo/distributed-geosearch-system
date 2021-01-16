package com.github.vitalibo.geosearch.subject.core;

import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class BufferedChannelTest {

    @Test(invocationCount = 5)
    public void testBatchSend() throws InterruptedException {
        TestBufferedChannel spyChannel = Mockito.spy(new TestBufferedChannel(1000, 3));
        Mockito.doNothing().when(spyChannel).send(Mockito.anyList());

        spyChannel.send("1");
        spyChannel.send("2");
        spyChannel.send("3");
        Mockito.verify(spyChannel).send(Arrays.asList("1", "2", "3"));
        spyChannel.send("4");
        Mockito.verify(spyChannel, Mockito.times(1)).send(Mockito.anyList());
        Thread.sleep(300);
        spyChannel.send("5");
        Thread.sleep(1000);
        Mockito.verify(spyChannel).send(Arrays.asList("4", "5"));
        Thread.sleep(500);
        spyChannel.send("6");
        spyChannel.send("7");
        spyChannel.send("8");
        spyChannel.send("9");
        spyChannel.send("10");
        Mockito.verify(spyChannel).send(Arrays.asList("6", "7", "8"));
        Mockito.verify(spyChannel, Mockito.never()).send(Arrays.asList("9", "10"));
        Thread.sleep(1300);
        Mockito.verify(spyChannel).send(Arrays.asList("9", "10"));
        Mockito.verify(spyChannel, Mockito.times(4)).send(Mockito.anyList());
    }

    private static class TestBufferedChannel extends BufferedChannel<String> {

        public TestBufferedChannel(int flashIntervalMillis, int maxBufferSize) {
            super(flashIntervalMillis, maxBufferSize);
        }

        @Override
        public void send(List<String> items) {
        }

    }

}
