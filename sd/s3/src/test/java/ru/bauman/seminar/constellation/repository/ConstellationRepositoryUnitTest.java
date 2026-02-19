package ru.bauman.seminar.constellation.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.bauman.seminar.constellation.entity.Constellation;
import ru.bauman.seminar.satellite.entity.EnergySystem;
import ru.bauman.seminar.satellite.entity.ext.CommunicationSatellite;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Юнит-тесты для ConstellationRepository (срезовые)")
class ConstellationRepositoryUnitTest {

	@Autowired
	private ConstellationRepository constellationRepository;

	@Test
	@DisplayName("Сохранение и поиск по ID")
	void shouldSaveAndFindById() {
		Constellation constellation = Constellation.builder()
				.name("Test")
				.description("Desc")
				.build();

		Constellation saved = constellationRepository.save(constellation);
		assertThat(saved.getId()).isNotNull();

		Optional<Constellation> found = constellationRepository.findById(saved.getId());
		assertThat(found).isPresent();
		assertThat(found.get().getName()).isEqualTo("Test");
	}

	@Test
	@DisplayName("Поиск по имени")
	void shouldFindByName() {
		Constellation constellation = Constellation.builder()
				.name("Unique")
				.build();
		constellationRepository.save(constellation);

		Optional<Constellation> found = constellationRepository.findByName("Unique");
		assertThat(found).isPresent();
		assertThat(found.get().getName()).isEqualTo("Unique");
	}

	@Test
	@DisplayName("Проверка existsByName")
	void shouldCheckExistsByName() {
		Constellation constellation = Constellation.builder()
				.name("Exists")
				.build();
		constellationRepository.save(constellation);

		boolean exists = constellationRepository.existsByName("Exists");
		assertThat(exists).isTrue();

		boolean notExists = constellationRepository.existsByName("Missing");
		assertThat(notExists).isFalse();
	}

	@Test
	@DisplayName("findAllWithSatellites загружает спутники")
	void shouldFindAllWithSatellites() {
		Constellation constellation = Constellation.builder()
				.name("WithSatellites")
				.build();

		CommunicationSatellite satellite = CommunicationSatellite.builder()
				.name("Comm")
				.energySystem(new EnergySystem(BigDecimal.valueOf(0.8)))
				.bandwidth(BigDecimal.valueOf(500))
				.constellation(constellation)
				.build();

		constellation.getSatellites().add(satellite);
		constellationRepository.save(constellation);

		List<Constellation> allWithSats = constellationRepository.findAllWithSatellites();
		assertThat(allWithSats).hasSize(1);
		assertThat(allWithSats.get(0).getSatellites()).hasSize(1);
		assertThat(allWithSats.get(0).getSatellites().get(0).getName()).isEqualTo("Comm");
	}
}