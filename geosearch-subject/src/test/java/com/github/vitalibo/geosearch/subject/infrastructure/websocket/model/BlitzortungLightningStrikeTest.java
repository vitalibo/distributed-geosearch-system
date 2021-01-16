package com.github.vitalibo.geosearch.subject.infrastructure.websocket.model;

import com.github.vitalibo.geosearch.subject.core.util.JsonSerDe;
import org.testng.Assert;
import org.testng.annotations.Test;

public class BlitzortungLightningStrikeTest {

    @Test
    public void testSerDe() {
        BlitzortungLightningStrike actual = JsonSerDe.fromJsonString(
            BlitzortungLightningStrikeTest.class.getResourceAsStream("/event/BlitzortungLightningStrike.json"),
            BlitzortungLightningStrike.class);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getTimestampInNanoSecond(), Long.valueOf(1609481738634303700L));
        Assert.assertEquals(actual.getLatitudeInDegree(), Double.valueOf(-29.379832));
        Assert.assertEquals(actual.getLongitudeInDegree(), Double.valueOf(142.623337));
        Assert.assertEquals(actual.getAltitudeInMeter(), Integer.valueOf(2139));
        Assert.assertEquals(actual.getPolarity(), Byte.valueOf((byte) 0));
        Assert.assertEquals(actual.getMaximalDeviationSpanInNanoSecond(), Long.valueOf(8767));
        Assert.assertEquals(actual.getMaximalCircularGapInDegree(), Integer.valueOf(207));
        Assert.assertEquals(actual.getStatus(), Integer.valueOf(0));
        Assert.assertEquals(actual.getRegion(), Integer.valueOf(2));
        Assert.assertEquals(actual.getSignals().size(), 27);
        BlitzortungSignal sig = actual.getSignals().get(0);
        Assert.assertEquals(sig.getStation(), Integer.valueOf(2353));
        Assert.assertEquals(sig.getTimestampDifferenceInNanoSecond(), Long.valueOf(2099271));
        Assert.assertEquals(sig.getLatitudeInDegree(), Double.valueOf(-33.021145));
        Assert.assertEquals(sig.getAltitudeInMeter(), Integer.valueOf(47));
        Assert.assertEquals(sig.getStatus(), Byte.valueOf((byte) 12));
    }

}
