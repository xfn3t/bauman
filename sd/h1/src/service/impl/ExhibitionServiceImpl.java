package service.impl;

import exception.NotFoundException;
import model.event.Exhibition;
import model.animal.Animal;
import model.animal.health.HealthStatus;
import repository.ExhibitionRepository;
import service.AnimalService;
import service.ExhibitionService;
import java.time.LocalDateTime;
import java.util.List;

public class ExhibitionServiceImpl implements ExhibitionService {

	private final AnimalService animalService;
	private final ExhibitionRepository exhibitionRepository;

	public ExhibitionServiceImpl(AnimalService animalService, ExhibitionRepository exhibitionRepository) {
		this.animalService = animalService;
		this.exhibitionRepository = exhibitionRepository;
	}

	@Override
	public void registerExhibition(Exhibition exhibition) {
		exhibitionRepository.save(exhibition);
		System.out.printf("Зарегистрирована выставка: %s (%s)%n",
				exhibition.getName(), exhibition.getStartTime().toLocalTime());
	}

	@Override
	public void registerAnimalForExhibition(String animalId, String exhibitionId) {
		Animal animal = animalService.findAnimal(animalId);
		Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
				.orElseThrow(() -> new NotFoundException("Выставка не найдена: " + exhibitionId));

		exhibition.addParticipant(animalId);
		exhibitionRepository.save(exhibition);

		System.out.printf("%s (%s) зарегистрирован(а) на выставку: %s%n",
				animal.getName(), animal.getSpeciesName(), exhibition.getName());
	}

	@Override
	public void cancelAnimalParticipation(String animalId, String exhibitionId, String reason) {
		Animal animal = animalService.findAnimal(animalId);
		Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
				.orElseThrow(() -> new NotFoundException("Выставка не найдена: " + exhibitionId));

		exhibition.removeParticipant(animalId);
		exhibitionRepository.save(exhibition);

		System.out.printf("Участие %s (%s) в выставке %s отменено: %s%n",
				animal.getName(), animal.getSpeciesName(), exhibition.getName(), reason);
	}

	@Override
	public void checkExhibitionReadiness(String exhibitionId) {
		Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
				.orElseThrow(() -> new NotFoundException("Выставка не найдена: " + exhibitionId));

		System.out.printf("Проверка готовности к выставке: %s%n", exhibition.getName());
		int healthyCount = 0;
		int sickCount = 0;

		for (String animalId : exhibition.getParticipantIds()) {
			try {
				Animal animal = animalService.findAnimal(animalId);
				if (animal.getHealthStatus() == HealthStatus.HEALTHY) {
					healthyCount++;
				} else {
					sickCount++;
					System.out.printf("  Внимание: %s (%s) - %s%n",
							animal.getName(), animal.getSpeciesName(),
							animal.getHealthStatus().getDescription());
				}
			} catch (NotFoundException e) {
				System.out.printf("  Ошибка: животное %s не найдено%n", animalId);
			}
		}

		System.out.printf("  Всего участников: %d (здоровы: %d, больны: %d)%n",
				exhibition.getParticipantCount(), healthyCount, sickCount);
	}

	@Override
	public List<Exhibition> getUpcomingExhibitions() {
		return exhibitionRepository.findUpcoming(LocalDateTime.now());
	}

	@Override
	public List<Exhibition> getTodaysExhibitions() {
		return exhibitionRepository.findByDate(LocalDateTime.now());
	}

	@Override
	public int getUpcomingExhibitionCount() {
		return getUpcomingExhibitions().size();
	}
}