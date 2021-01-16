package com.github.vitalibo.geosearch.processor.core.util;

import com.github.vitalibo.geosearch.processor.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class MapSerDeTest {

    @Test
    public void testSerDe() {
        Map<String, Integer> map = new HashMap<>();
        map.put("foo", 1);
        map.put("bar", 2);

        Map<String, Integer> actual = TestHelper.assertSerDe(SerDe.HashMap(SerDe.String(), SerDe.Integer()), map);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, map);
        Assert.assertNotSame(actual, map);
        Assert.assertTrue(actual instanceof HashMap);
    }


    @Test(expectedExceptions = NullPointerException.class)
    public void testSerDeNotNullable() {
        Map<String, Integer> map = new HashMap<>();
        map.put("foo", 1);
        map.put("bar", null);

        TestHelper.assertSerDe(SerDe.HashMap(SerDe.String(), SerDe.Integer()), map);
    }

}
