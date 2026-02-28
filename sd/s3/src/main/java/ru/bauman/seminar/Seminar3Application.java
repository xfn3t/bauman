package ru.bauman.seminar;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.bauman.seminar.constellation.controller.dto.request.ConstellationRequest;
import ru.bauman.seminar.constellation.controller.dto.response.ConstellationResponse;
import ru.bauman.seminar.constellation.controller.dto.response.ConstellationStatusDto;
import ru.bauman.seminar.constellation.service.ConstellationService;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteResponse;
import ru.bauman.seminar.satellite.entity.SatelliteType;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@SpringBootApplication
public class Seminar3Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Seminar3Application.class, args);

		ConstellationService constellationService = context.getBean(ConstellationService.class);

		log.info("\n🔄 Очистка базы данных...");
		constellationService.findAll().forEach(c -> constellationService.delete(c.id()));
		log.info("✅ База данных очищена.\n");

		log.info("СОЗДАНИЕ СПЕЦИАЛИЗИРОВАННЫХ СПУТНИКОВ:");
		log.info("---------------------------------------------");

		ConstellationResponse orbit1 = constellationService.create(
				new ConstellationRequest("Орбита-1", "Основная группировка связи"));
		ConstellationResponse orbit2 = constellationService.create(
				new ConstellationRequest("Орбита-2", "Резервная группировка"));

		log.info("Создана спутниковая группировка: {}", orbit1.name());
		log.info("Создана спутниковая группировка: {}", orbit2.name());
		log.info("---------------------------------------------\n");

		// 2. Добавление спутников
		log.info("📡 ДОБАВЛЕНИЕ СПУТНИКОВ:");

		// Спутники для Орбита-1
		SatelliteRequest comm1 = new SatelliteRequest("Связь-1", BigDecimal.valueOf(0.85),
				SatelliteType.COMMUNICATION, BigDecimal.valueOf(500.0), null);
		SatelliteRequest img1 = new SatelliteRequest("ДЗЗ-1", BigDecimal.valueOf(0.92),
				SatelliteType.IMAGING, null, BigDecimal.valueOf(2.5));
		SatelliteRequest img2 = new SatelliteRequest("ДЗЗ-2", BigDecimal.valueOf(0.45),
				SatelliteType.IMAGING, null, BigDecimal.valueOf(1.0));

		SatelliteRequest comm2 = new SatelliteRequest("Связь-2", BigDecimal.valueOf(0.75),
				SatelliteType.COMMUNICATION, BigDecimal.valueOf(1000.0), null);
		SatelliteRequest img3 = new SatelliteRequest("ДЗЗ-3", BigDecimal.valueOf(0.15),
				SatelliteType.IMAGING, null, BigDecimal.valueOf(0.5));

		constellationService.addSatellite(orbit1.id(), comm1);
		log.info("Связь-1 добавлен в группировку 'Орбита-1'");

		constellationService.addSatellite(orbit1.id(), img1);
		log.info("ДЗЗ-1 добавлен в группировку 'Орбита-1'");

		constellationService.addSatellite(orbit1.id(), img2);
		log.info("ДЗЗ-2 добавлен в группировку 'Орбита-1'");

		constellationService.addSatellite(orbit2.id(), comm2);
		log.info("Связь-2 добавлен в группировку 'Орбита-2'");

		constellationService.addSatellite(orbit2.id(), img3);
		log.info("ДЗЗ-3 добавлен в группировку 'Орбита-2'");

		log.info("-----------------------------------\n");

		log.info("=== АКТИВАЦИЯ СПУТНИКОВ В ГРУППИРОВКЕ: Орбита-1 ===");
		List<SatelliteResponse> activated = constellationService.activateAllSatellites(orbit1.id());
		activated.forEach(sat -> log.info("✅ {}: Активация успешна", sat.name()));
		log.info("");

		log.info("=== ВЫПОЛНЕНИЕ МИССИЙ ДЛЯ ГРУППИРОВКИ: Орбита-1 ===");
		log.info("ВЫПОЛНЕНИЕ МИССИЙ ГРУППИРОВКИ ОРБИТА-1");
		log.info("==================================================");
		List<SatelliteResponse> missionResults = constellationService.executeAllMissions(orbit1.id());

		log.info("");

		log.info("=== СТАТУС ГРУППИРОВКИ: Орбита-1 ===");
		ConstellationStatusDto status = constellationService.getConstellationStatus("Орбита-1");
		log.info(status.getName());

		log.info("=== ВСЕ ГРУППИРОВКИ (СОДЕРЖИМОЕ РЕПОЗИТОРИЯ) ===");
		List<ConstellationResponse> all = constellationService.findAll();
		all.forEach(c -> {
			log.info("Группировка: {} (id={})", c.name(), c.id());
			log.info("  Описание: {}", c.description());
			log.info("  Спутники:");
			c.satellites().forEach(s -> {
				StringBuilder sb = new StringBuilder();
				sb.append(String.format("    - %s (id=%d, тип=%s, заряд=%.0f%%, состояние=%s",
						s.name(), s.id(), s.type(),
						s.batteryLevel().multiply(BigDecimal.valueOf(100)),
						s.state()));
				if (s.bandwidth() != null) {
					sb.append(String.format(", пропускная способность=%.1f Мбит/с", s.bandwidth()));
				}
				if (s.resolution() != null) {
					sb.append(String.format(", разрешение=%.1f м/пиксель, снимков=%d",
							s.resolution(), s.photosTaken()));
				}
				log.info(sb.toString());
			});
			log.info("");
		});

		// context.close();
	}
}