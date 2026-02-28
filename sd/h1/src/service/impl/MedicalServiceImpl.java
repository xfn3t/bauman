package service.impl;

import model.animal.Animal;
import model.animal.health.HealthStatus;
import model.animal.health.MedicalRecord;
import model.employee.Employee;
import service.AnimalService;
import service.MedicalService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicalServiceImpl implements MedicalService {

	private final AnimalService animalService;

	public MedicalServiceImpl(AnimalService animalService) {
		this.animalService = animalService;
	}

	@Override
	public void performMedicalCheckup(String animalId, Employee veterinarian, String notes) {

		Animal animal = animalService.findAnimal(animalId);

		MedicalRecord record = new MedicalRecord(
				LocalDate.now(),
				HealthStatus.HEALTHY,
				notes,
				veterinarian.getName()
		);
		animal.addMedicalRecord(record);

		System.out.printf("Ветеринар %s провел осмотр %s. Результат: %s%n",
				veterinarian.getName(),
				animal.getName(),
				notes
		);
	}

	@Override
	public List<Animal> getAnimalsNeedingCheckup() {

		List<Animal> result = new ArrayList<>();
		LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6); // каждые полгода

		for (Animal animal : animalService.getAllAnimals()) {
			LocalDate lastCheckup = animal.getMedicalRecords().stream()
					.map(MedicalRecord::getDate)
					.max(LocalDate::compareTo)
					.orElse(LocalDate.MIN);

			if (lastCheckup.isBefore(sixMonthsAgo)) {
				result.add(animal);
			}
		}
		return result;
	}

	@Override
	public void updateHealthStatus(String animalId, HealthStatus status, String reason) {

		Animal animal = animalService.findAnimal(animalId);

		MedicalRecord record = new MedicalRecord(
				LocalDate.now(),
				status,
				reason,
				"Система"
		);
		animal.addMedicalRecord(record);
	}
}