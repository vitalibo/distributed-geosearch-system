package com.github.vitalibo.geosearch.processor.core.util;

import com.github.vitalibo.geosearch.processor.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

public class CollectionSerDeTest {

    @Test
    public void testSerDeCollection() {
        Collection<String> collection = Arrays.asList("foo", "bar", "baz");

        Collection<String> actual = TestHelper.assertSerDe(SerDe.Collection(SerDe.String()), collection);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, collection);
        Assert.assertNotSame(actual, collection);
    }

    @Test
    public void testSerDeArrayList() {
        List<String> list = new ArrayList<>(Arrays.asList("foo", "bar", "baz"));

        List<String> actual = TestHelper.assertSerDe(SerDe.ArrayList(SerDe.String()), list);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, list);
        Assert.assertNotSame(actual, list);
        Assert.assertTrue(actual instanceof ArrayList);
    }

    @Test
    public void testSerDeLinkedList() {
        List<String> list = new LinkedList<>(Arrays.asList("foo", "bar", "baz"));

        List<String> actual = TestHelper.assertSerDe(SerDe.LinkedList(SerDe.String()), list);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, list);
        Assert.assertNotSame(actual, list);
        Assert.assertTrue(actual instanceof LinkedList);
    }

    @Test
    public void testSerDeHashSet() {
        Set<String> list = new HashSet<>(Arrays.asList("foo", "bar", "baz"));

        Set<String> actual = TestHelper.assertSerDe(SerDe.HashSet(SerDe.String()), list);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, list);
        Assert.assertNotSame(actual, list);
        Assert.assertTrue(actual instanceof HashSet);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testSerDeCollectionNotNull() {
        Collection<String> collection = Arrays.asList("foo", "bar", null, "baz");

        TestHelper.assertSerDe(SerDe.Collection(SerDe.String()), collection);
    }

}
