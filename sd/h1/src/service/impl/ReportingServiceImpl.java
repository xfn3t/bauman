package service.impl;

import model.animal.Animal;
import model.animal.health.HealthStatus;
import model.animal.health.MedicalRecord;
import service.AnimalService;
import service.FeedingService;
import service.ReportingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportingServiceImpl implements ReportingService {

	private final AnimalService animalService;
	private final FeedingService feedingService;

	public ReportingServiceImpl(AnimalService animalService, FeedingService feedingService) {
		this.animalService = animalService;
		this.feedingService = feedingService;
	}

	@Override
	public void generateDailyReport() {
		System.out.println("\nОтчет зоопарка: ");
		System.out.printf("Всего животных: %d%n", animalService.getTotalAnimals());

		Map<String, Long> animalsBySpecies = animalService.getAllAnimals().stream()
				.collect(Collectors.groupingBy(Animal::getSpeciesName, Collectors.counting()));

		System.out.println("\nЖивотные по видам:");
		animalsBySpecies.forEach((species, count) ->
				System.out.printf("  %s: %d%n", species, count));

		System.out.println("\nСтатус здоровья:");
		Map<HealthStatus, Long> healthStats = animalService.getAllAnimals().stream()
				.collect(Collectors.groupingBy(Animal::getHealthStatus, Collectors.counting()));

		healthStats.forEach((status, count) ->
				System.out.printf("  %s: %d%n", status.getDescription(), count));

		long fedToday = animalService.getAllAnimals().stream()
				.filter(animal -> feedingService.isAnimalFedToday(animal.getId()))
				.count();

		System.out.printf("%nПокормлено сегодня: %d из %d%n", fedToday, animalService.getTotalAnimals());

		List<Animal> needCheckup = getAnimalsNeedingCheckup();
		if (!needCheckup.isEmpty()) {
			System.out.println("\nТребуют медосмотра:");
			needCheckup.forEach(animal ->
					System.out.printf("  %s (%s)%n", animal.getName(), animal.getSpeciesName()));
		}
	}

	@Override
	public void generateAnimalReport(String animalId) {

		Animal animal = animalService.findAnimal(animalId);

		System.out.println("\nОтчет по животному: ");
		System.out.printf("ID: %s%n", animal.getId());
		System.out.printf("Имя: %s%n", animal.getName());
		System.out.printf("Вид: %s%n", animal.getSpeciesName());
		System.out.printf("Дата поступления: %s%n", animal.getArrivalDate());
		System.out.printf("Статус здоровья: %s%n", animal.getHealthStatus().getDescription());
		System.out.printf("Инструкции по кормлению: %s%n", animal.getFeedingInstructions());
		System.out.printf("Инструкции по уборке: %s%n", animal.getCleaningInstructions());

		if (!animal.getMedicalRecords().isEmpty()) {
			System.out.println("\nМедицинские записи:");
			animal.getMedicalRecords().forEach(record ->
					System.out.printf("  %s: %s (%s)%n",
							record.getDate(),
							record.getNotes(),
							record.getVeterinarian()));
		}
	}

	private List<Animal> getAnimalsNeedingCheckup() {
		List<Animal> result = new ArrayList<>();
		java.time.LocalDate sixMonthsAgo = java.time.LocalDate.now().minusMonths(6);

		for (Animal animal : animalService.getAllAnimals()) {
			java.time.LocalDate lastCheckup = animal.getMedicalRecords().stream()
					.map(MedicalRecord::getDate)
					.max(java.time.LocalDate::compareTo)
					.orElse(java.time.LocalDate.MIN);

			if (lastCheckup.isBefore(sixMonthsAgo)) {
				result.add(animal);
			}
		}
		return result;
	}
}