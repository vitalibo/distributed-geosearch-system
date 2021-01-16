package com.github.vitalibo.geosearch.subject.core.source;

import com.github.vitalibo.geosearch.subject.core.model.GeoEvent;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class RandomGeoEventSourceTest {

    @Mock
    private Supplier<Double> mockDoubleSupplier;
    @Mock
    private Supplier<Instant> mockInstant;
    @Mock
    private Supplier<UUID> mockUUID;

    private RandomGeoEventSource source;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        source = new RandomGeoEventSource(10, 0.5, mockDoubleSupplier, mockInstant, mockUUID);
    }

    @Test
    public void testProcessEmpty() {
        Mockito.when(mockDoubleSupplier.get()).thenReturn(0.6);

        Optional<GeoEvent> actual = source.process();

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, Optional.empty());
    }

    @Test
    public void testProcess() {
        Mockito.when(mockDoubleSupplier.get()).thenReturn(0.4, 0.71231, 0.13942);
        Mockito.when(mockInstant.get()).thenReturn(Instant.ofEpochSecond(1609719052));
        Mockito.when(mockUUID.get()).thenReturn(UUID.fromString("829df179-ab16-4d31-a3d0-4100709f24c9"));

        Optional<GeoEvent> actual = source.process();

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.isPresent());
        GeoEvent event = actual.get();
        Assert.assertEquals(event.getId(), UUID.fromString("829df179-ab16-4d31-a3d0-4100709f24c9"));
        Assert.assertEquals(event.getTimestamp(), Instant.ofEpochSecond(1609719052));
        Assert.assertEquals(event.getLatitude(), Double.valueOf(38.2158));
        Assert.assertEquals(event.getLongitude(), -129.8088, 0.00001);
    }

}
