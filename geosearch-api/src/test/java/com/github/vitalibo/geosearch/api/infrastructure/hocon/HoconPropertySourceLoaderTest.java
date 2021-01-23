package com.github.vitalibo.geosearch.api.infrastructure.hocon;

import com.github.vitalibo.geosearch.api.TestHelper;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HoconPropertySourceLoaderTest {

    private HoconPropertySourceLoader propertySourceLoader;

    @BeforeMethod
    public void setUp() {
        propertySourceLoader = new HoconPropertySourceLoader();
    }

    @Test
    public void testLoad() throws IOException {
        List<PropertySource<?>> list = propertySourceLoader.load(
            "config", new ClassPathResource(TestHelper.resourcePath("config.hocon")));

        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), 1);
        PropertySource<?> propertySource = list.get(0);
        Map<String, ?> actual = (Map<String, ?>) propertySource.getSource();
        Assert.assertEquals(actual.size(), 12);
        Assert.assertEquals(actual.get("x"), "y");
        Assert.assertEquals(actual.get("app.double"), 1.23);
        Assert.assertEquals(actual.get("app.arr2[1][1]"), "gpu");
        Assert.assertEquals(actual.get("app.arr2[1][0]"), "cpu");
        Assert.assertEquals(actual.get("app.foo"), "bar");
        Assert.assertEquals(actual.get("app.properties.f1.f2.f3"), "com.apple.eio.FileManager");
        Assert.assertEquals(actual.get("app.bool"), true);
        Assert.assertEquals(actual.get("app.arr[2]"), "arr3");
        Assert.assertEquals(actual.get("app.arr2[0].os.name"), "linux");
        Assert.assertEquals(actual.get("app.arr[1]"), "arr2");
        Assert.assertEquals(actual.get("app.arr[0]"), "arr1");
        Assert.assertEquals(actual.get("app.int"), 1);
    }

    @Test
    public void testGetFileExtensions() {
        String[] actual = propertySourceLoader.getFileExtensions();

        Assert.assertNotNull(actual);
        Assert.assertEquals(Arrays.asList(actual), Arrays.asList("hocon", "conf"));
    }

}
