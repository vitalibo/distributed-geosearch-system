package com.github.vitalibo.geosearch.processor.core.stream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.vitalibo.geosearch.processor.TestHelper;
import com.github.vitalibo.geosearch.processor.TestInputTopic;
import com.github.vitalibo.geosearch.processor.TestOutputTopic;
import com.github.vitalibo.geosearch.processor.TestTopology;
import com.github.vitalibo.geosearch.processor.core.model.GeoEvent;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchQuery;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchResult;
import com.github.vitalibo.geosearch.processor.core.util.SerDe;
import org.apache.kafka.streams.KeyValue;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class GeoSearchTopologyTest extends TestTopology {

    private TestInputTopic<Integer, GeoEvent> inputGeoEvent;
    private TestInputTopic<String, GeoSearchQuery> inputGeoSearchQuery;
    private TestOutputTopic<String, GeoSearchResult> outputGeoSearchResult;

    @BeforeMethod
    public void setUp() {
        inputGeoEvent = createMockInputTopic(SerDe.Integer(), SerDe.GeoEvent());
        inputGeoSearchQuery = createMockInputTopic(SerDe.String(), SerDe.GeoSearchQuery());
        outputGeoSearchResult = createMockOutputTopic(SerDe.String(), SerDe.GeoSearchResult());
        configure(new GeoSearchTopology(inputGeoEvent, inputGeoSearchQuery, outputGeoSearchResult, 5));
    }

    @Test
    public void testTopology() {
        List<List<KeyValue<Integer, GeoEvent>>> events = TestHelper.resourceAsMultiListKeyValue(
            TestHelper.resourcePath("GeoEvent.json"), new TypeReference<>() {});
        List<List<KeyValue<String, GeoSearchQuery>>> queries = TestHelper.resourceAsMultiListKeyValue(
            TestHelper.resourcePath("GeoSearchQuery.json"), new TypeReference<>() {});
        List<List<KeyValue<String, GeoSearchResult>>> expected = TestHelper.resourceAsMultiListKeyValue(
            TestHelper.resourcePath("GeoSearchResult.json"), new TypeReference<>() {});

        Assert.assertEquals(events.size(), queries.size());
        Assert.assertEquals(events.size(), expected.size());
        for (int i = 0; i < events.size(); i++) {
            inputGeoEvent.pipeKeyValueList(events.get(i));
            inputGeoSearchQuery.pipeKeyValueList(queries.get(i));

            List<KeyValue<String, GeoSearchResult>> actual = outputGeoSearchResult.readKeyValuesToList();
            Assert.assertEquals(actual, expected.get(i), String.format("Iteration [%s]", i));
        }
    }

}
