package com.github.vitalibo.geosearch.subject.infrastructure.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.function.Function;

public class KafkaChannelTest {

    @Mock
    private KafkaProducer<String, Integer> mockKafkaProducer;
    @Mock
    private Function<String, ProducerRecord<String, Integer>> mockTranslator;
    @Mock
    private ProducerRecord<String, Integer> mockProducerRecord;

    private KafkaChannel<String, Integer, String> channel;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        channel = new KafkaChannel<>(mockKafkaProducer, mockTranslator);
    }

    @Test
    public void testSend() {
        Mockito.when(mockTranslator.apply(Mockito.any())).thenReturn(mockProducerRecord);

        channel.send("foo");

        Mockito.verify(mockTranslator).apply("foo");
        Mockito.verify(mockKafkaProducer).send(mockProducerRecord);
    }

    @Test
    public void testClose() {
        channel.close();

        Mockito.verify(mockKafkaProducer).close();
    }

}
