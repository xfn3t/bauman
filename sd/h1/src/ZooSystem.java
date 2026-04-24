import model.animal.*;
import model.animal.health.HealthStatus;
import model.animal.impl.Lion;
import model.animal.impl.Parrot;
import model.animal.impl.Snake;
import model.employee.impl.Veterinarian;
import model.employee.impl.Zookeeper;
import model.enclosure.Enclosure;
import model.event.Exhibition;
import repository.AnimalRepository;
import repository.EnclosureRepository;
import repository.ExhibitionRepository;
import repository.FeedingLogRepository;
import repository.impl.InMemoryAnimalRepositoryImpl;
import repository.impl.InMemoryEnclosureRepositoryImpl;
import repository.impl.InMemoryExhibitionRepositoryImpl;
import repository.impl.InMemoryFeedingLogRepositoryImpl;
import service.AnimalService;
import service.EnclosureService;
import service.ExhibitionService;
import service.FeedingService;
import service.MedicalService;
import service.ReportingService;
import service.impl.AnimalServiceImpl;
import service.impl.EnclosureServiceImpl;
import service.impl.ExhibitionServiceImpl;
import service.impl.FeedingServiceImpl;
import service.impl.MedicalServiceImpl;
import service.impl.ReportingServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

public class ZooSystem {

	public static void main(String[] args) {

		AnimalRepository animalRepository = new InMemoryAnimalRepositoryImpl();
		FeedingLogRepository feedingLogRepository = new InMemoryFeedingLogRepositoryImpl();
		EnclosureRepository enclosureRepository = new InMemoryEnclosureRepositoryImpl();
		ExhibitionRepository exhibitionRepository = new InMemoryExhibitionRepositoryImpl();

		AnimalService animalService = new AnimalServiceImpl(animalRepository);
		FeedingService feedingService = new FeedingServiceImpl(animalService, feedingLogRepository);
		MedicalService medicalService = new MedicalServiceImpl(animalService);
		EnclosureService enclosureService = new EnclosureServiceImpl(enclosureRepository);
		ExhibitionService exhibitionService = new ExhibitionServiceImpl(animalService, exhibitionRepository);
		ReportingService reportingService = new ReportingServiceImpl(animalService, feedingService);

		Zookeeper keeper = new Zookeeper("K1", "Иван Петров", "keeper@zoo.ru");
		Veterinarian vet = new Veterinarian("V1", "Мария Сидорова", "vet@zoo.ru");

		Enclosure lionEnclosure = new Enclosure("E1", "Львиный вольер", 500.0);
		Enclosure birdEnclosure = new Enclosure("E2", "Птичий павильон", 200.0);
		Enclosure reptileEnclosure = new Enclosure("E3", "Террариум", 100.0);

		enclosureService.registerEnclosure(lionEnclosure);
		enclosureService.registerEnclosure(birdEnclosure);
		enclosureService.registerEnclosure(reptileEnclosure);

		Lion simba = new Lion("L1", "Симба");
		Lion nala = new Lion("L2", "Нала");
		Parrot kesha = new Parrot("P1", "Кеша");
		Parrot gosha = new Parrot("P2", "Гоша");
		Snake kaa = new Snake("S1", "Каа");
		Snake python = new Snake("S2", "Питон");

		animalService.registerAnimal(simba, lionEnclosure);
		animalService.registerAnimal(nala, lionEnclosure);
		animalService.registerAnimal(kesha, birdEnclosure);
		animalService.registerAnimal(gosha, birdEnclosure);
		animalService.registerAnimal(kaa, reptileEnclosure);
		animalService.registerAnimal(python, reptileEnclosure);

		medicalService.performMedicalCheckup("L1", vet, "Здоров, все прививки сделаны");
		medicalService.performMedicalCheckup("P1", vet, "Здоров, клюв в норме");
		medicalService.performMedicalCheckup("S1", vet, "Здоров, кожа чистая");

		medicalService.performMedicalCheckup("L2", vet, "Травма лапы");
		medicalService.updateHealthStatus("L2", HealthStatus.SICK, "Открытая рана на лапе");

		medicalService.performMedicalCheckup("P2", vet, "Простуда");
		medicalService.updateHealthStatus("P2", HealthStatus.SICK, "Температура, насморк");

		medicalService.performMedicalCheckup("S2", vet, "Паразиты");
		medicalService.updateHealthStatus("S2", HealthStatus.SICK, "Обнаружены гельминты");

		feedingService.feedAnimal("L1", keeper);
		feedingService.feedAnimal("P1", keeper);
		feedingService.feedAnimal("S1", keeper);

		feedingService.feedAnimal("L1", keeper);
		feedingService.feedAnimal("L2", keeper);

		enclosureService.cleanEnclosure("E1", keeper);
		enclosureService.cleanEnclosure("E2", keeper);

		enclosureService.markEnclosureAsDirty("E1", "Лев разбросал еду");
		enclosureService.cleanEnclosure("E1", keeper);

		Exhibition morningShow = new Exhibition("EX1", "Утреннее шоу хищников", LocalDateTime.now().plusHours(2), 60);
		Exhibition birdShow = new Exhibition("EX2", "Шоу птиц", LocalDateTime.now().plusHours(4), 45);

		exhibitionService.registerExhibition(morningShow);
		exhibitionService.registerExhibition(birdShow);

		exhibitionService.registerAnimalForExhibition("L1", "EX1");
		exhibitionService.registerAnimalForExhibition("L2", "EX1");
		exhibitionService.registerAnimalForExhibition("P1", "EX2");
		exhibitionService.registerAnimalForExhibition("P2", "EX2");

		exhibitionService.checkExhibitionReadiness("EX1");
		exhibitionService.checkExhibitionReadiness("EX2");

		exhibitionService.cancelAnimalParticipation("L2", "EX1", "Травма лапы");
		exhibitionService.cancelAnimalParticipation("P2", "EX2", "Простуда");

		reportingService.generateDailyReport();

		reportingService.generateAnimalReport("L1");
		reportingService.generateAnimalReport("L2");
		reportingService.generateAnimalReport("P1");

		List<Animal> hungryAnimals = feedingService.getAnimalsNeedingFeeding();
		if (hungryAnimals.isEmpty()) {
			System.out.println("Все животные накормлены!");
		} else {
			hungryAnimals.forEach(animal -> {
				System.out.printf("- %s (%s): последнее кормление - %s%n",
						animal.getName(),
						animal.getSpeciesName(),
						animal.getLastFeedingTime() != null ? animal.getLastFeedingTime().toLocalTime() : "никогда"
				);
			});
		}

		List<Enclosure> dirtyEnclosures = enclosureService.getDirtyEnclosures();
		if (dirtyEnclosures.isEmpty()) {
			System.out.println("Все вольеры чистые!");
		} else {
			dirtyEnclosures.forEach(enclosure -> System.out.println("- " + enclosure.getName()));
		}

		System.out.printf("Всего животных: %d%n", animalService.getTotalAnimals());
		System.out.printf("Львов: %d%n", animalService.getAnimalsBySpecies(Species.LION).size());
		System.out.printf("Попугаев: %d%n", animalService.getAnimalsBySpecies(Species.PARROT).size());
		System.out.printf("Змей: %d%n", animalService.getAnimalsBySpecies(Species.SNAKE).size());

		System.out.printf("Покормлено сегодня: %d животных%n", feedingService.getTodayFeedingLog().size());
		System.out.printf("Очищено вольеров: %d из %d%n", enclosureService.getCleanedTodayCount(), enclosureService.getAllEnclosures().size());
		System.out.printf("Запланировано выставок: %d%n", exhibitionService.getUpcomingExhibitionCount());

		System.out.println("Лев говорит: " + simba.roar());
		System.out.println("Попугай говорит: " + kesha.talk());
		System.out.println("Инструкции по кормлению льва: " + simba.getFeedingInstructions());
		System.out.println("Инструкции по уборке для змеи: " + kaa.getCleaningInstructions());
	}
}