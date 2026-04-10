package service.impl;

import model.animal.Animal;
import model.employee.Employee;
import model.feeding.FeedingLogEntry;
import repository.FeedingLogRepository;
import service.AnimalService;
import service.FeedingService;

import java.time.LocalDateTime;
import java.util.List;

public class FeedingServiceImpl implements FeedingService {

	private final AnimalService animalService;
	private final FeedingLogRepository feedingLogRepository;

	public FeedingServiceImpl(AnimalService animalService,
							  FeedingLogRepository feedingLogRepository) {
		this.animalService = animalService;
		this.feedingLogRepository = feedingLogRepository;
	}

	@Override
	public void feedAnimal(String animalId, Employee employee) {

		Animal animal = animalService.findAnimal(animalId);

		feedingLogRepository.logFeeding(animalId, LocalDateTime.now(), employee.getName());
		animal.feed();

		System.out.printf("%s покормил(а) %s (%s). Инструкции: %s%n",
				employee.getName(),
				animal.getName(),
				animal.getSpeciesName(),
				animal.getFeedingInstructions());
	}

	@Override
	public void feedAnimals(List<String> animalIds, Employee employee) {
		for (String animalId : animalIds) {
			try {
				feedAnimal(animalId, employee);
			} catch (Exception e) {
				System.out.printf("Ошибка при кормлении %s: %s%n", animalId, e.getMessage());
			}
		}
	}

	@Override
	public boolean isAnimalFedToday(String animalId) {
		return feedingLogRepository.getLastFeedingTime(animalId)
				.map(lastFed -> lastFed.toLocalDate().equals(LocalDateTime.now().toLocalDate()))
				.orElse(false);
	}

	@Override
	public List<Animal> getAnimalsNeedingFeeding() {
		List<Animal> allAnimals = animalService.getAllAnimals();
		allAnimals.removeIf(animal -> !animal.isHungry());
		return allAnimals;
	}

	@Override
	public List<FeedingLogEntry> getTodayFeedingLog() {
		return feedingLogRepository.getFeedingLogForDay(LocalDateTime.now());
	}

	@Override
	public List<FeedingLogEntry> getAnimalFeedingHistory(String animalId) {
		return feedingLogRepository.getFeedingLogForAnimal(animalId);
	}
}