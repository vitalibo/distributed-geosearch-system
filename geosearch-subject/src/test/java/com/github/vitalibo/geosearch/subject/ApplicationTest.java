package com.github.vitalibo.geosearch.subject;

import com.github.vitalibo.geosearch.subject.core.Runner;
import com.github.vitalibo.geosearch.subject.core.Source;
import com.github.vitalibo.geosearch.subject.core.model.GeoEvent;
import com.github.vitalibo.geosearch.subject.infrastructure.Factory;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ApplicationTest {

    @Mock
    private Factory mockFactory;
    @Mock
    private Source<GeoEvent> mockSource;
    @Mock
    private Runner<GeoEvent> mockRunner;

    private Application application;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        Mockito.when(mockFactory.createRandomGeoEventSource()).thenReturn(mockSource);
        Mockito.when(mockFactory.createBlitzortungLightningSource()).thenReturn(mockSource);
        Mockito.when(mockFactory.createRunner(Mockito.any())).thenReturn(mockRunner);
        application = new Application(mockFactory);
    }

    @Test
    public void testProcessRandom() {
        application.run(new String[]{"random"});

        Mockito.verify(mockFactory).createRandomGeoEventSource();
        Mockito.verify(mockFactory, Mockito.never()).createBlitzortungLightningSource();
        Mockito.verify(mockFactory).createRunner(mockSource);
        Mockito.verify(mockRunner).process();
    }

    @Test
    public void testProcessBlitzortungLightningSource() {
        application.run(new String[]{"blitzortung"});

        Mockito.verify(mockFactory, Mockito.never()).createRandomGeoEventSource();
        Mockito.verify(mockFactory).createBlitzortungLightningSource();
        Mockito.verify(mockFactory).createRunner(mockSource);
        Mockito.verify(mockRunner).process();
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "Unsupported source type")
    public void testProcessUnknownSource() {
        IllegalArgumentException actual = Assert.expectThrows(IllegalArgumentException.class,
            () -> application.run(new String[]{"foo"}));

        Mockito.verify(mockFactory, Mockito.never()).createRandomGeoEventSource();
        Mockito.verify(mockFactory, Mockito.never()).createRunner(mockSource);
        Mockito.verify(mockRunner, Mockito.never()).process();
        throw actual;
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "foo")
    public void testProcessFailed() {
        Mockito.doThrow(new RuntimeException("foo")).when(mockRunner).process();

        application.run(new String[]{"random"});
    }

}
