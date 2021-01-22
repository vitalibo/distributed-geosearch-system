package com.github.vitalibo.geosearch.api.core;

import com.github.vitalibo.geosearch.api.core.model.GeoSearchCommand;
import com.github.vitalibo.geosearch.api.core.util.ErrorState;
import lombok.SneakyThrows;
import org.geotools.geojson.geom.GeometryJSON;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

import java.io.IOException;

public final class ValidationRules {

    private static final GeometryJSON geometryJson = new GeometryJSON();

    private ValidationRules() {
    }

    public static void verifyBoundingBoxIsValidGeoJson(GeoSearchCommand command, ErrorState errorState) {
        if (command.isDisconnect()) {
            return;
        }

        try {
            geometryJson.read(command.getBoundingBox());
        } catch (IOException ignored) {
            errorState.addError("body", "Invalid GeoJSON");
        }
    }

    @SneakyThrows
    public static void verifyBoundingBoxContainsPolygon(GeoSearchCommand command, ErrorState errorState) {
        if (command.isDisconnect()) {
            return;
        }

        Polygon polygon = geometryJson.readPolygon(command.getBoundingBox());
        if (polygon.isEmpty()) {
            errorState.addError("geojson", "Feature geometry should be contains polygon");
        }
    }

    @SneakyThrows
    public static void verifyPolygonHasCorrectGeoCoordinates(GeoSearchCommand command, ErrorState errorState) {
        if (command.isDisconnect()) {
            return;
        }

        Polygon polygon = geometryJson.readPolygon(command.getBoundingBox());
        Coordinate[] coordinates = polygon.getCoordinates();
        for (int i = 0; i < coordinates.length; i++) {
            double latitude = coordinates[i].y;
            if (latitude < -90 || latitude > 90) {
                errorState.addError(
                    String.format("polygon[%s].latitude", i),
                    String.format("Latitude should be in range [-90; 90]. Current value %s", latitude));
            }

            double longitude = coordinates[i].x;
            if (longitude < -180 || longitude > 180) {
                errorState.addError(
                    String.format("polygon[%s].longitude", i),
                    String.format("Longitude should be in range [-180; 180]. Current value %s", longitude));
            }
        }
    }

}
