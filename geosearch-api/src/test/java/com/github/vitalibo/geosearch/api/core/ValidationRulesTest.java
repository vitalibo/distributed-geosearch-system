package com.github.vitalibo.geosearch.api.core;

import com.github.vitalibo.geosearch.api.TestHelper;
import com.github.vitalibo.geosearch.api.core.model.GeoSearchCommand;
import com.github.vitalibo.geosearch.api.core.util.ErrorState;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ValidationRulesTest {

    @Mock
    private GeoSearchCommand mockGeoSearchCommand;

    private ErrorState errorState;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        errorState = new ErrorState();
    }

    @Test
    public void testVerifyBoundingBoxIsValidGeoJson() {
        GeoSearchCommand command = new GeoSearchCommand()
            .withBoundingBox(TestHelper.resourceAsString(TestHelper.resourcePath("sample.json")));

        ValidationRules.verifyBoundingBoxIsValidGeoJson(command, errorState);

        Assert.assertFalse(errorState.hasErrors());
    }

    @Test
    public void testVerifyBoundingBoxContainsPolygon() {
        GeoSearchCommand command = new GeoSearchCommand()
            .withBoundingBox(TestHelper.resourceAsString(TestHelper.resourcePath("sample.json")));

        ValidationRules.verifyBoundingBoxContainsPolygon(command, errorState);

        Assert.assertFalse(errorState.hasErrors());
    }

    @Test
    public void testVerifyPolygonHasCorrectGeoCoordinates() {
        GeoSearchCommand command = new GeoSearchCommand()
            .withBoundingBox(TestHelper.resourceAsString(TestHelper.resourcePath("sample.json")));

        ValidationRules.verifyPolygonHasCorrectGeoCoordinates(command, errorState);

        Assert.assertFalse(errorState.hasErrors());
    }

    @DataProvider
    public Object[][] samplesVerifyBoundingBoxIsValidGeoJson() {
        return new Object[][]{
            {""}, {"foo"}, {"{}"}, {TestHelper.resourceAsString(TestHelper.resourcePath("sample.geojson"))}
        };
    }

    @Test(dataProvider = "samplesVerifyBoundingBoxIsValidGeoJson")
    public void testVerifyBoundingBoxIsValidGeoJsonFail(String bb) {
        GeoSearchCommand command = new GeoSearchCommand()
            .withBoundingBox(bb);

        ValidationRules.verifyBoundingBoxIsValidGeoJson(command, errorState);

        Assert.assertTrue(errorState.hasErrors());
    }

    @DataProvider
    public Object[][] samplesVerifyBoundingBoxContainsPolygon() {
        return new Object[][]{
            {TestHelper.resourceAsString(TestHelper.resourcePath("sample.json"))},
            {TestHelper.resourceAsString(TestHelper.resourcePath("sample_empty.json"))}
        };
    }

    @Test(dataProvider = "samplesVerifyBoundingBoxContainsPolygon")
    public void testVerifyBoundingBoxContainsPolygonFail(String bb) {
        GeoSearchCommand command = new GeoSearchCommand()
            .withBoundingBox(bb);

        ValidationRules.verifyBoundingBoxContainsPolygon(command, errorState);

        Assert.assertTrue(errorState.hasErrors());
    }

    @DataProvider
    public Object[][] samplesVerifyPolygonHasCorrectGeoCoordinates() {
        return new Object[][]{
            {TestHelper.resourceAsString(TestHelper.resourcePath("sample_1.json"))},
            {TestHelper.resourceAsString(TestHelper.resourcePath("sample_2.json"))},
            {TestHelper.resourceAsString(TestHelper.resourcePath("sample_3.json"))},
            {TestHelper.resourceAsString(TestHelper.resourcePath("sample_4.json"))},
        };
    }

    @Test(dataProvider = "samplesVerifyPolygonHasCorrectGeoCoordinates")
    public void testVerifyPolygonHasCorrectGeoCoordinatesFail(String bb) {
        GeoSearchCommand command = new GeoSearchCommand()
            .withBoundingBox(bb);

        ValidationRules.verifyPolygonHasCorrectGeoCoordinates(command, errorState);

        Assert.assertTrue(errorState.hasErrors());
    }

    @Test
    public void testVerifyBoundingBoxIsValidGeoJsonWhenDisconnect() {
        Mockito.when(mockGeoSearchCommand.isDisconnect()).thenReturn(true);

        ValidationRules.verifyBoundingBoxIsValidGeoJson(mockGeoSearchCommand, errorState);

        Mockito.verify(mockGeoSearchCommand, Mockito.never()).getBoundingBox();
        Assert.assertFalse(errorState.hasErrors());
    }

    @Test
    public void testVerifyBoundingBoxContainsPolygonWhenDisconnect() {
        Mockito.when(mockGeoSearchCommand.isDisconnect()).thenReturn(true);

        ValidationRules.verifyBoundingBoxContainsPolygon(mockGeoSearchCommand, errorState);

        Mockito.verify(mockGeoSearchCommand, Mockito.never()).getBoundingBox();
        Assert.assertFalse(errorState.hasErrors());
    }

    @Test
    public void testVerifyPolygonHasCorrectGeoCoordinatesWhenDisconnect() {
        Mockito.when(mockGeoSearchCommand.isDisconnect()).thenReturn(true);

        ValidationRules.verifyPolygonHasCorrectGeoCoordinates(mockGeoSearchCommand, errorState);

        Mockito.verify(mockGeoSearchCommand, Mockito.never()).getBoundingBox();
        Assert.assertFalse(errorState.hasErrors());
    }

}
