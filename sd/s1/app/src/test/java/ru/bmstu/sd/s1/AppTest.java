package ru.bmstu.sd.s1;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import ru.bmstu.sd.s1.integration.IntegrationTest;
import ru.bmstu.sd.s1.satellite.CommunicationSatelliteTest;
import ru.bmstu.sd.s1.satellite.ImagingSatelliteTest;
import ru.bmstu.sd.s1.satellite.SatelliteConstellationTest;
import ru.bmstu.sd.s1.satellite.SatelliteTest;


@Suite
@SelectClasses({
        SatelliteTest.class,
        ImagingSatelliteTest.class,
        CommunicationSatelliteTest.class,
        SatelliteConstellationTest.class,
        IntegrationTest.class
})
class AppTest {
}