package com.github.vitalibo.connect.elasticsearch;

import org.apache.kafka.common.cache.Cache;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GeoPointTest {

    @Mock
    private Cache<Schema, Schema> mockCache;
    @Mock
    private Supplier<Cache<Schema, Schema>> mockSupplier;

    private Map<String, Object> props;
    private GeoPoint<SinkRecord> transformation;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        Mockito.when(mockSupplier.get()).thenReturn(mockCache);
        props = new HashMap<>();
        props.put("field.latitude", "latitude");
        props.put("field.longitude", "longitude");
        transformation = new GeoPoint<>(mockSupplier);
    }

    @Test
    public void testConfigure() {
        transformation.configure(props);

        Mockito.verify(mockSupplier).get();
    }

    @Test
    public void testApplyTombstone() {
        SinkRecord tombstoneRecord = record(null, null);

        transformation.configure(props);
        SinkRecord actual = transformation.apply(tombstoneRecord);

        Assert.assertNotNull(actual);
        Assert.assertSame(actual, tombstoneRecord);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testApplySchemaLess() {
        Map<String, Object> obj = new HashMap<>();
        obj.put("foo", "bar");
        obj.put("latitude", 12.34);
        obj.put("longitude", 43.21);
        SinkRecord record = record(obj, null);

        transformation.configure(props);
        SinkRecord actual = transformation.apply(record);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.topic(), "topic-name");
        Assert.assertEquals(actual.kafkaPartition(), Integer.valueOf(1));
        Assert.assertEquals(actual.keySchema(), Schema.STRING_SCHEMA);
        Assert.assertEquals(actual.key(), "key-value");
        Assert.assertNull(actual.valueSchema());
        Assert.assertEquals(actual.kafkaOffset(), 1234L);
        Map<String, Object> value = (Map<String, Object>) actual.value();
        Assert.assertEquals(value.get("foo"), "bar");
        Assert.assertEquals(value.get("latitude"), 12.34);
        Assert.assertEquals(value.get("longitude"), 43.21);
        Assert.assertEquals(value.get("location"), new HashMap<String, Double>() {{
            put("lat", 12.34);
            put("lon", 43.21);
        }});
        Assert.assertEquals(value.size(), 4);
    }

    @Test
    public void testApplyWithSchema() {
        final Struct struct = new Struct(
            SchemaBuilder.struct()
                .field("foo", Schema.STRING_SCHEMA)
                .field("latitude", Schema.FLOAT64_SCHEMA)
                .field("longitude", Schema.FLOAT64_SCHEMA)
                .build())
            .put("foo", "bar")
            .put("latitude", 12.34)
            .put("longitude", 43.21);
        SinkRecord record = record(struct, struct.schema());

        transformation.configure(props);
        SinkRecord actual = transformation.apply(record);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.topic(), "topic-name");
        Assert.assertEquals(actual.kafkaPartition(), Integer.valueOf(1));
        Assert.assertEquals(actual.keySchema(), Schema.STRING_SCHEMA);
        Assert.assertEquals(actual.key(), "key-value");
        Assert.assertEquals(actual.kafkaOffset(), 1234L);
        final Schema schema = actual.valueSchema();
        Assert.assertNotNull(schema);
        Assert.assertEquals(schema, SchemaBuilder.struct()
            .field("foo", Schema.STRING_SCHEMA)
            .field("latitude", Schema.FLOAT64_SCHEMA)
            .field("longitude", Schema.FLOAT64_SCHEMA)
            .field("location", SchemaBuilder.struct()
                .field("lat", Schema.FLOAT64_SCHEMA)
                .field("lon", Schema.FLOAT64_SCHEMA)
                .build())
            .build());
        final Struct value = (Struct) actual.value();
        Assert.assertNotNull(value);
        Assert.assertSame(value.schema(), actual.valueSchema());
        Assert.assertEquals(value.getString("foo"), "bar");
        Assert.assertEquals(value.getFloat64("latitude"), Double.valueOf(12.34));
        Assert.assertEquals(value.getFloat64("longitude"), Double.valueOf(43.21));
        final Struct location = value.getStruct("location");
        Assert.assertNotNull(location);
        Assert.assertEquals(location.getFloat64("lat"), Double.valueOf(12.34));
        Assert.assertEquals(location.getFloat64("lon"), Double.valueOf(43.21));
        Mockito.verify(mockCache).get(struct.schema());
        Mockito.verify(mockCache).put(struct.schema(), actual.valueSchema());
    }

    @Test
    public void testApplyWithSchemaUseCache() {
        final Struct struct = new Struct(
            SchemaBuilder.struct()
                .field("foo", Schema.STRING_SCHEMA)
                .field("latitude", Schema.FLOAT64_SCHEMA)
                .field("longitude", Schema.FLOAT64_SCHEMA)
                .build())
            .put("foo", "bar")
            .put("latitude", 12.34)
            .put("longitude", 43.21);
        final Schema schema = SchemaBuilder.struct()
            .field("foo", Schema.STRING_SCHEMA)
            .field("latitude", Schema.FLOAT64_SCHEMA)
            .field("longitude", Schema.FLOAT64_SCHEMA)
            .field("location", SchemaBuilder.struct()
                .field("lat", Schema.FLOAT64_SCHEMA)
                .field("lon", Schema.FLOAT64_SCHEMA)
                .build())
            .build();
        SinkRecord record = record(struct, struct.schema());
        Mockito.when(mockCache.get(struct.schema())).thenReturn(schema);

        transformation.configure(props);
        SinkRecord actual = transformation.apply(record);

        Assert.assertNotNull(actual);
        Assert.assertSame(actual.valueSchema(), schema);
        Mockito.verify(mockCache).get(struct.schema());
        Mockito.verify(mockCache, Mockito.never()).put(Mockito.any(), Mockito.any());
    }

    @Test
    public void testConfig() {
        ConfigDef actual = transformation.config();

        Assert.assertNotNull(actual);
        Map<String, ConfigDef.ConfigKey> keys = actual.configKeys();
        Assert.assertNotNull(keys.get("field.latitude"));
        Assert.assertNotNull(keys.get("field.longitude"));
        Assert.assertNotNull(keys.get("field.new"));
    }

    private static SinkRecord record(Object value, Schema valueSchema) {
        return new SinkRecord("topic-name", 1, Schema.STRING_SCHEMA, "key-value", valueSchema, value, 1234L);
    }

}
