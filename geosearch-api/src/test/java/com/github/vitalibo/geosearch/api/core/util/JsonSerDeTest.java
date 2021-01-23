package com.github.vitalibo.geosearch.api.core.util;


import com.fasterxml.jackson.core.type.TypeReference;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.Map;

public class JsonSerDeTest {

    @Test
    public void testToJsonString() {
        String actual = JsonSerDe.toJsonString(
            Collections.singletonMap("foo", "bar"));

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, "{\"foo\":\"bar\"}");
    }

    @Test
    public void testFromJsonString() {
        String jsonString = "{\"foo\":\"bar\"}";

        Map actual = JsonSerDe.fromJsonString(jsonString, Map.class);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.get("foo"), "bar");
    }

    @Test
    public void testFromJsonBytes() {
        byte[] bytes = "{\"foo\":\"bar\"}".getBytes();

        Map actual = JsonSerDe.fromJsonString(bytes, Map.class);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.get("foo"), "bar");
    }

    @Test
    public void testFromJsonInputStream() {
        ByteArrayInputStream stream = new ByteArrayInputStream("{\"foo\":\"bar\"}".getBytes());

        Map actual = JsonSerDe.fromJsonString(stream, Map.class);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.get("foo"), "bar");
    }

    @Test
    public void testFromJsonStringTypeReference() {
        String jsonString = "{\"foo\":\"bar\"}";

        Map<String, String> actual = JsonSerDe.fromJsonString(jsonString, new TestType());

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.get("foo"), "bar");
    }

    @Test
    public void testFromJsonBytesTypeReference() {
        byte[] bytes = "{\"foo\":\"bar\"}".getBytes();

        Map<String, String> actual = JsonSerDe.fromJsonString(bytes, new TestType());

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.get("foo"), "bar");
    }

    @Test
    public void testFromJsonInputStreamTypeReference() {
        ByteArrayInputStream stream = new ByteArrayInputStream("{\"foo\":\"bar\"}".getBytes());

        Map<String, String> actual = JsonSerDe.fromJsonString(stream, new TestType());

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.get("foo"), "bar");
    }


    private static class TestType extends TypeReference<Map<String, String>> {
    }

}
