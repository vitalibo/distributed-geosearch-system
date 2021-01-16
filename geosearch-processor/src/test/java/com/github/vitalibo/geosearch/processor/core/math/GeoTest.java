package com.github.vitalibo.geosearch.processor.core.math;

import com.github.vitalibo.geosearch.processor.TestHelper;
import com.github.vitalibo.geosearch.processor.core.model.BoundingBox;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GeoTest {

    @Test
    public void testEncodeGeoHash() {
        String actual = Geo.encodeGeoHash(49.80751915261102, 24.00378388141459, 12);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, "u8c565pjbdw8");
    }

    @Test
    public void testCreatePolygonGeoJson() {
        BoundingBox boundingBox = new BoundingBox();
        boundingBox.setType("GeoJson");
        boundingBox.setGeometry(TestHelper.resourceAsString(TestHelper.resourcePath("Geometry.geojson")));

        Polygon actual = Geo.createPolygon(boundingBox);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getArea(), 5.091248829185833E-5);
    }

    @Test
    public void testCreatePolygonWKT() {
        BoundingBox boundingBox = new BoundingBox();
        boundingBox.setType("WKT");
        boundingBox.setGeometry(TestHelper.resourceAsString(TestHelper.resourcePath("Geometry.wkt")));

        Polygon actual = Geo.createPolygon(boundingBox);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getArea(), 5.091248829185833E-5);
    }

    @Test(expectedExceptions = IllegalStateException.class,
        expectedExceptionsMessageRegExp = "Unsupported geometry type.")
    public void testCreatePolygonUnsupportedType() {
        BoundingBox boundingBox = new BoundingBox();
        boundingBox.setType("XML");
        boundingBox.setGeometry("<xml></xml>");

        Geo.createPolygon(boundingBox);
    }

    @Test
    public void testCreatePoint() {
        Point actual = Geo.createPoint(49.80751915261102, 24.00378388141459);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getX(), 24.00378388141459);
        Assert.assertEquals(actual.getY(), 49.80751915261102);
    }

    @Test
    public void testCoverBoundingBox() {
        BoundingBox boundingBox = new BoundingBox();
        boundingBox.setType("GeoJson");
        boundingBox.setGeometry(TestHelper.resourceAsString(TestHelper.resourcePath("Volodymyra_Velykoho-Naukova.geojson")));
        Polygon polygon = Geo.createPolygon(boundingBox);

        Set<String> actual = Geo.coverBoundingBox(polygon, 6);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, new HashSet<>(Arrays.asList(
            "u8c56h", "u8c56k", "u8c56s", "u8c565", "u8c564", "u8c561", "u8c567", "u8c566", "u8c563", "u8c56e", "u8c56d", "u8c569",
            "u8c53e", "u8c53d", "u8c53g", "u8c53f", "u8c53c", "u8c539", "u8c53s", "u8c53u")));
    }

}
