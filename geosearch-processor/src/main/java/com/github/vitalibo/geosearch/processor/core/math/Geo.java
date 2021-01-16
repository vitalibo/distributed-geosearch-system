package com.github.vitalibo.geosearch.processor.core.math;

import com.github.davidmoten.geo.Coverage;
import com.github.davidmoten.geo.GeoHash;
import com.github.vitalibo.geosearch.processor.core.model.BoundingBox;
import lombok.SneakyThrows;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.WKTReader;

import java.io.StringReader;
import java.util.Set;

public final class Geo {

    private static final GeometryJSON geometryJson = new GeometryJSON();
    private static final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
    private static final WKTReader geometryWkt = new WKTReader(geometryFactory);

    private Geo() {
    }

    public static String encodeGeoHash(double latitude, double longitude, int geoHashLength) {
        return GeoHash.encodeHash(latitude, longitude, geoHashLength);
    }

    @SneakyThrows
    public static Polygon createPolygon(BoundingBox boundingBox) {
        switch (boundingBox.getType().toLowerCase()) {
            case "geojson":
                return geometryJson.readPolygon(new StringReader(boundingBox.getGeometry()));
            case "wkt":
                return (Polygon) geometryWkt.read(boundingBox.getGeometry());
            default:
                throw new IllegalStateException("Unsupported geometry type.");
        }
    }

    public static Point createPoint(double latitude, double longitude) {
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

    public static Set<String> coverBoundingBox(Polygon polygon, int geoHashLength) {
        Envelope envelope = polygon.getEnvelopeInternal();
        Coverage coverage = GeoHash.coverBoundingBox(
            envelope.getMaxY(), envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), geoHashLength);
        return coverage.getHashes();
    }

}
