package com.github.vitalibo.geosearch.subject.infrastructure.websocket.model.transform;

import com.github.vitalibo.geosearch.subject.core.model.GeoEvent;
import com.github.vitalibo.geosearch.subject.infrastructure.websocket.model.BlitzortungLightningStrike;
import com.github.vitalibo.geosearch.subject.infrastructure.websocket.model.BlitzortungSignal;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Supplier;

public class GeoEventTranslatorTest {

    @Mock
    private Supplier<UUID> mockUUID;

    private GeoEventTranslator translator;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        translator = new GeoEventTranslator(mockUUID);
    }

    @Test
    public void testFrom() {
        BlitzortungLightningStrike event = new BlitzortungLightningStrike()
            .withTimestampInNanoSecond(1609481738634303700L)
            .withLatitudeInDegree(-29.379832)
            .withLongitudeInDegree(142.623337)
            .withAltitudeInMeter(0)
            .withPolarity((byte) 0)
            .withMaximalDeviationSpanInNanoSecond(8767L)
            .withMaximalCircularGapInDegree(207)
            .withStatus(0)
            .withRegion(2)
            .withSignals(Arrays.asList(
                new BlitzortungSignal()
                    .withStation(2353)
                    .withTimestampDifferenceInNanoSecond(2099271L)
                    .withLatitudeInDegree(-33.021145)
                    .withLongitudeInDegree(137.557159)
                    .withAltitudeInMeter(47)
                    .withStatus((byte) 12),
                new BlitzortungSignal()
                    .withStation(2438)
                    .withTimestampDifferenceInNanoSecond(2381239L)
                    .withLatitudeInDegree(-34.874317)
                    .withLongitudeInDegree(138.694046)
                    .withAltitudeInMeter(104)
                    .withStatus((byte) 12)))
            .withDelay(5.0);

        GeoEvent actual = translator.from(event);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getTimestamp(), Instant.parse("2021-01-01T06:15:38.634Z"));
        Assert.assertEquals(actual.getLatitude(), Double.valueOf(-29.379832));
        Assert.assertEquals(actual.getLongitude(), Double.valueOf(142.623337));
    }

}
