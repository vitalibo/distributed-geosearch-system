package com.github.vitalibo.connect.elasticsearch;

import org.apache.kafka.common.cache.Cache;
import org.apache.kafka.common.cache.LRUCache;
import org.apache.kafka.common.cache.SynchronizedCache;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.Requirements;
import org.apache.kafka.connect.transforms.util.SchemaUtil;
import org.apache.kafka.connect.transforms.util.SimpleConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GeoPoint<R extends ConnectRecord<R>> implements Transformation<R> {

    private static final ConfigDef CONFIG_DEF = new ConfigDef()
        .define(ConfigName.LATITUDE_FIELD, ConfigDef.Type.STRING, null, ConfigDef.Importance.HIGH,
            "Field name to extract latitude.")
        .define(ConfigName.LONGITUDE_FIELD, ConfigDef.Type.STRING, null, ConfigDef.Importance.HIGH,
            "Field name to extract longitude.")
        .define(ConfigName.NEW_FIELD, ConfigDef.Type.STRING, "location", ConfigDef.Importance.MEDIUM,
            "New field name to insert geo point.");

    private static final Schema GEO_POINT_SCHEMA = SchemaBuilder.struct()
        .field(ESName.LATITUDE, Schema.FLOAT64_SCHEMA)
        .field(ESName.LONGITUDE, Schema.FLOAT64_SCHEMA)
        .build();

    private static final String PURPOSE = "geo point field insertion";

    private final Supplier<Cache<Schema, Schema>> schemaCacheSupplier;

    private String latFieldName;
    private String lonFieldName;
    private String newFieldName;
    private Cache<Schema, Schema> schemaUpdateCache;

    public GeoPoint() {
        this(() -> new SynchronizedCache<>(new LRUCache<>(16)));
    }

    GeoPoint(Supplier<Cache<Schema, Schema>> schemaCacheSupplier) {
        this.schemaCacheSupplier = schemaCacheSupplier;
    }

    @Override
    public void configure(Map<String, ?> props) {
        final SimpleConfig config = new SimpleConfig(CONFIG_DEF, props);
        latFieldName = config.getString(ConfigName.LATITUDE_FIELD);
        lonFieldName = config.getString(ConfigName.LONGITUDE_FIELD);
        newFieldName = config.getString(ConfigName.NEW_FIELD);
        schemaUpdateCache = schemaCacheSupplier.get();
    }

    @Override
    public R apply(R record) {
        if (record.value() == null) {
            return record;
        } else if (record.valueSchema() == null) {
            return applySchemaLess(record);
        } else {
            return applyWithSchema(record);
        }
    }

    private R applySchemaLess(R record) {
        final Map<String, Object> value = Requirements.requireMap(record.value(), PURPOSE);
        final Map<String, Object> updatedValue = new HashMap<>(value);

        final Map<String, Double> geoPoint = new HashMap<>();
        geoPoint.put(ESName.LATITUDE, (Double) value.get(latFieldName));
        geoPoint.put(ESName.LONGITUDE, (Double) value.get(lonFieldName));
        updatedValue.put(newFieldName, geoPoint);

        return newRecord(record, null, updatedValue);
    }

    private R applyWithSchema(R record) {
        final Struct value = Requirements.requireStruct(record.value(), PURPOSE);
        Schema updatedSchema = schemaUpdateCache.get(value.schema());
        if (updatedSchema == null) {
            updatedSchema = makeUpdatedSchema(value.schema());
            schemaUpdateCache.put(value.schema(), updatedSchema);
        }

        final Struct updatedValue = new Struct(updatedSchema);
        for (Field field : value.schema().fields()) {
            updatedValue.put(field.name(), value.get(field));
        }

        final Struct geoPoint = new Struct(GEO_POINT_SCHEMA);
        geoPoint.put(ESName.LATITUDE, value.getFloat64(latFieldName));
        geoPoint.put(ESName.LONGITUDE, value.getFloat64(lonFieldName));
        updatedValue.put(newFieldName, geoPoint);

        return newRecord(record, updatedSchema, updatedValue);
    }

    private R newRecord(R record, Schema updatedSchema, Object updatedValue) {
        return record.newRecord(
            record.topic(),
            record.kafkaPartition(),
            record.keySchema(),
            record.key(),
            updatedSchema,
            updatedValue,
            record.timestamp());
    }

    private Schema makeUpdatedSchema(Schema schema) {
        final SchemaBuilder builder = SchemaUtil.copySchemaBasics(schema, SchemaBuilder.struct());
        for (Field field : schema.fields()) {
            builder.field(field.name(), field.schema());
        }

        builder.field(newFieldName, GEO_POINT_SCHEMA);
        return builder.build();
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public void close() {
        schemaUpdateCache = null;
    }

    private interface ConfigName {
        String LATITUDE_FIELD = "field.latitude";
        String LONGITUDE_FIELD = "field.longitude";
        String NEW_FIELD = "field.new";
    }

    private interface ESName {
        String LATITUDE = "lat";
        String LONGITUDE = "lon";
    }

}
