package com.github.vitalibo.geosearch.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

public final class TestHelper {

    private TestHelper() {
    }

    public static String resourceAsString(String resource) {
        return new BufferedReader(new InputStreamReader(resourceAsInputStream(resource)))
            .lines()
            .collect(Collectors.joining(System.lineSeparator()));
    }

    public static InputStream resourceAsInputStream(String resource) {
        InputStream stream = TestHelper.class.getResourceAsStream(resource);
        Objects.requireNonNull(stream, String.format("Resource do not exists. '%s'", resource));
        return stream;
    }

    public static String resourcePath(String resource) {
        final StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
        return File.separator +
            String.join(File.separator,
                stack.getClassName().replace('.', File.separatorChar),
                stack.getMethodName(),
                resource);
    }

}
