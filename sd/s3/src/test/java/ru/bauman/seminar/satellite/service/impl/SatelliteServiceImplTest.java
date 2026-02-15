package ru.bauman.seminar.satellite.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteResponse;
import ru.bauman.seminar.satellite.entity.SatelliteType;
import ru.bauman.seminar.satellite.entity.ext.CommunicationSatellite;
import ru.bauman.seminar.satellite.entity.ext.ImagingSatellite;
import ru.bauman.seminar.satellite.mapper.SatelliteMapper;
import ru.bauman.seminar.satellite.service.entity.SatelliteEntityService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SatelliteServiceImplTest {

	@Mock
	private SatelliteEntityService satelliteEntityService;

	@Mock
	private SatelliteMapper satelliteMapper;

	// Эти зависимости не используются в тестах, но нужны для конструктора
	@Mock
	private List<ru.bauman.seminar.satellite.creator.SatelliteCreator> creators;
	@Mock
	private List<ru.bauman.seminar.satellite.updater.SatelliteUpdater> updaters;

	@InjectMocks
	private SatelliteServiceImpl satelliteService;

	@Test
	void activate_shouldActivateWhenBatterySufficient() {
		CommunicationSatellite satellite = CommunicationSatellite.builder()
				.id(1L)
				.name("Test")
				.batteryLevel(new BigDecimal("0.3"))
				.active(false)
				.bandwidth(new BigDecimal("500.0"))
				.build();

		SatelliteResponse expectedResponse = new SatelliteResponse(
				1L, "Test", new BigDecimal("0.3"), true, SatelliteType.COMMUNICATION,
				new BigDecimal("500.0"), null, 0
		);

		when(satelliteEntityService.findById(1L)).thenReturn(satellite);
		when(satelliteEntityService.save(satellite)).thenReturn(satellite);
		when(satelliteMapper.toResponse(satellite)).thenReturn(expectedResponse);

		SatelliteResponse actual = satelliteService.activate(1L);

		assertTrue(satellite.getActive());
		verify(satelliteEntityService).save(satellite);
		assertSame(expectedResponse, actual);
	}

	@Test
	void activate_shouldNotActivateWhenBatteryLow() {
		CommunicationSatellite satellite = CommunicationSatellite.builder()
				.id(1L)
				.name("Test")
				.batteryLevel(new BigDecimal("0.1"))
				.active(false)
				.bandwidth(new BigDecimal("500.0"))
				.build();

		SatelliteResponse expectedResponse = new SatelliteResponse(
				1L, "Test", new BigDecimal("0.1"), false, SatelliteType.COMMUNICATION,
				new BigDecimal("500.0"), null, 0
		);

		when(satelliteEntityService.findById(1L)).thenReturn(satellite);
		when(satelliteMapper.toResponse(satellite)).thenReturn(expectedResponse);

		SatelliteResponse actual = satelliteService.activate(1L);

		assertFalse(satellite.getActive());
		verify(satelliteEntityService, never()).save(any());
		assertSame(expectedResponse, actual);
	}

	@Test
	void performMission_shouldDecreaseBatteryAndDeactivateIfCritical() {
		CommunicationSatellite satellite = CommunicationSatellite.builder()
				.id(1L)
				.name("Test")
				.batteryLevel(new BigDecimal("0.2"))
				.active(true)
				.bandwidth(new BigDecimal("500.0"))
				.build();

		// После выполнения миссии заряд станет 0.15, активность false
		SatelliteResponse expectedResponse = new SatelliteResponse(
				1L, "Test", new BigDecimal("0.15"), false, SatelliteType.COMMUNICATION,
				new BigDecimal("500.0"), null, 0
		);

		when(satelliteEntityService.findById(1L)).thenReturn(satellite);
		// В performMission нет вызова save, полагаемся на @Transactional
		when(satelliteMapper.toResponse(satellite)).thenReturn(expectedResponse);

		SatelliteResponse actual = satelliteService.performMission(1L);

		assertEquals(new BigDecimal("0.15"), satellite.getBatteryLevel());
		assertFalse(satellite.getActive());
		verify(satelliteEntityService, never()).save(any());
		assertSame(expectedResponse, actual);
	}

	@Test
	void performMission_forImagingSatellite_incrementsPhotosTaken() {
		ImagingSatellite satellite = ImagingSatellite.builder()
				.id(2L)
				.name("Img")
				.batteryLevel(new BigDecimal("0.9"))
				.active(true)
				.resolution(new BigDecimal("2.5"))
				.photosTaken(0)
				.build();

		SatelliteResponse expectedResponse = new SatelliteResponse(
				2L, "Img", new BigDecimal("0.82"), true, SatelliteType.IMAGING,
				null, new BigDecimal("2.5"), 1
		);

		when(satelliteEntityService.findById(2L)).thenReturn(satellite);
		when(satelliteMapper.toResponse(satellite)).thenReturn(expectedResponse);

		SatelliteResponse actual = satelliteService.performMission(2L);

		assertEquals(1, satellite.getPhotosTaken());
		assertEquals(new BigDecimal("0.82"), satellite.getBatteryLevel());
		verify(satelliteEntityService, never()).save(any());
		assertSame(expectedResponse, actual);
	}
}