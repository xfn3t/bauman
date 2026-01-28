package ru.bmstu.sd.s2;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import ru.bmstu.sd.s2.integration.IntegrationTest;
import ru.bmstu.sd.s2.satellite.*;


@Suite
@SelectClasses({
		EnergySystemTest.class,
		SatelliteTest.class,
        SatelliteTest.class,
        ImagingSatelliteTest.class,
        CommunicationSatelliteTest.class,
        SatelliteConstellationTest.class,
        IntegrationTest.class
})
class AppTest {
}