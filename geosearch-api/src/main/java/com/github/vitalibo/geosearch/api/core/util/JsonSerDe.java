package com.github.vitalibo.geosearch.api.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.InputStream;

public class JsonSerDe {

    @Getter
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private JsonSerDe() {
        super();
    }

    @SneakyThrows
    public static <T> String toJsonString(T value) {
        return objectMapper.writeValueAsString(value);
    }

    @SneakyThrows
    public static <T> T fromJsonString(String json, Class<T> clazz) {
        return objectMapper.readValue(json, clazz);
    }

    @SneakyThrows
    public static <T> T fromJsonString(byte[] json, Class<T> clazz) {
        return objectMapper.readValue(json, clazz);
    }

    @SneakyThrows
    public static <T> T fromJsonString(InputStream stream, Class<T> clazz) {
        return objectMapper.readValue(stream, clazz);
    }

    @SneakyThrows
    public static <T> T fromJsonString(String json, TypeReference<T> type) {
        return objectMapper.readValue(json, type);
    }

    @SneakyThrows
    public static <T> T fromJsonString(byte[] json, TypeReference<T> type) {
        return objectMapper.readValue(json, type);
    }

    @SneakyThrows
    public static <T> T fromJsonString(InputStream stream, TypeReference<T> type) {
        return objectMapper.readValue(stream, type);
    }

}
