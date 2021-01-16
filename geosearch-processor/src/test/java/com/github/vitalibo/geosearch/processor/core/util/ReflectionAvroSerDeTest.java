package com.github.vitalibo.geosearch.processor.core.util;

import com.github.vitalibo.geosearch.processor.TestHelper;
import lombok.Data;
import org.apache.avro.reflect.Nullable;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ReflectionAvroSerDeTest {

    @Test
    public void testSerDe() {
        MyObject obj = new MyObject();
        obj.setFirst("foo");
        obj.setSecond(123);

        MyObject actual = TestHelper.assertSerDe(new ReflectionAvroSerDe<>(MyObject.class), obj);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, obj);
        Assert.assertNotSame(actual, obj);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testSerDeNotNullable() {
        MyObject obj = new MyObject();
        obj.setFirst("foo");

        TestHelper.assertSerDe(new ReflectionAvroSerDe<>(MyObject.class), obj);
    }

    @Data
    public static class MyObject {

        private String first;
        private Integer second;
        @Nullable
        private Double third;

    }

}
