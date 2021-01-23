package com.github.vitalibo.geosearch.api.infrastructure.kafka;

import com.github.vitalibo.geosearch.api.core.model.GeoSearchCommand;
import com.github.vitalibo.geosearch.api.infrastructure.kafka.model.transform.ProducerRecordTranslator;
import com.github.vitalibo.geosearch.shared.GeoSearchCommandShared;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class KafkaGeoSearchEngineTest {

    @Mock
    private KafkaTemplate<String, GeoSearchCommandShared> mockKafkaTemplate;
    @Mock
    private ProducerRecordTranslator mockProducerRecordTranslator;
    @Mock
    private ProducerRecord<String, GeoSearchCommandShared> mockProducerRecord;

    private KafkaGeoSearchEngine kafkaGeoSearchEngine;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        kafkaGeoSearchEngine = new KafkaGeoSearchEngine(mockKafkaTemplate, mockProducerRecordTranslator);
    }

    @Test
    public void testSubmit() {
        Mockito.when(mockProducerRecordTranslator.from(Mockito.any())).thenReturn(mockProducerRecord);
        GeoSearchCommand command = new GeoSearchCommand();
        command.setSessionId("foo");

        kafkaGeoSearchEngine.submit(command);

        Mockito.verify(mockProducerRecordTranslator).from(command);
        Mockito.verify(mockKafkaTemplate).send(mockProducerRecord);
    }

}
