package com.github.vitalibo.geosearch.processor.core.stream.transform;

import com.github.vitalibo.geosearch.processor.TestHelper;
import com.github.vitalibo.geosearch.processor.core.model.BoundingBox;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchCommand;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

public class CoverBoundingBoxTransformerTest {

    @Mock
    private KeyValueStore<String, Set<String>> mockKeyValueStore;
    @Mock
    private ProcessorContext mockProcessorContext;

    private CoverBoundingBoxTransformer transformer;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        Mockito.when(mockProcessorContext.getStateStore(Mockito.anyString())).thenReturn(mockKeyValueStore);
        transformer = new CoverBoundingBoxTransformer(5, "store name");
        transformer.init(mockProcessorContext);
    }

    @Test
    public void testTransformAdd() {
        GeoSearchCommand request = new GeoSearchCommand()
            .withId("74382c9d-b771-48af-a0df-4040d2a42f49")
            .withSubscribe(true)
            .withBoundingBox(new BoundingBox()
                .withType("geojson")
                .withGeometry(TestHelper.resourceAsString(TestHelper.resourcePath("Avtovokzal-Bronova.geojson"))));

        Iterable<KeyValue<String, GeoSearchCommand>> iterable = transformer.transform("foo", request);

        Assert.assertNotNull(iterable);
        List<KeyValue<String, GeoSearchCommand>> objects = new ArrayList<>();
        iterable.forEach(objects::add);
        Collections.sort(objects, Comparator.comparing(a -> a.key));
        Assert.assertEquals(objects.size(), 2);
        Assert.assertEquals(objects.get(0).key, "u8c54");
        Assert.assertEquals(objects.get(0).value, request);
        Assert.assertEquals(objects.get(1).key, "u8c56");
        Assert.assertEquals(objects.get(1).value, request);
        Mockito.verify(mockProcessorContext).getStateStore("store name");
        Mockito.verify(mockKeyValueStore).put("74382c9d-b771-48af-a0df-4040d2a42f49", Set.of("u8c54", "u8c56"));
        Mockito.verify(mockKeyValueStore, Mockito.never()).delete(Mockito.anyString());
    }

    @Test
    public void testTransformRemove() {
        Mockito.when(mockKeyValueStore.delete(Mockito.anyString())).thenReturn(Set.of("u8c54", "u8c56"));
        GeoSearchCommand request = new GeoSearchCommand()
            .withId("74382c9d-b771-48af-a0df-4040d2a42f49")
            .withSubscribe(false);

        Iterable<KeyValue<String, GeoSearchCommand>> iterable = transformer.transform("foo", request);

        Assert.assertNotNull(iterable);
        List<KeyValue<String, GeoSearchCommand>> objects = new ArrayList<>();
        iterable.forEach(objects::add);
        Collections.sort(objects, Comparator.comparing(a -> a.key));
        Assert.assertEquals(objects.size(), 2);
        Assert.assertEquals(objects.get(0).key, "u8c54");
        Assert.assertEquals(objects.get(0).value, request);
        Assert.assertEquals(objects.get(1).key, "u8c56");
        Assert.assertEquals(objects.get(1).value, request);
        Mockito.verify(mockProcessorContext).getStateStore("store name");
        Mockito.verify(mockKeyValueStore, Mockito.never()).put(Mockito.anyString(), Mockito.anySet());
        Mockito.verify(mockKeyValueStore).delete("74382c9d-b771-48af-a0df-4040d2a42f49");
    }

}
