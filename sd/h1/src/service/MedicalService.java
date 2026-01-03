package service;

import model.animal.Animal;
import model.animal.health.HealthStatus;
import model.employee.Employee;

import java.util.List;

public interface MedicalService {
	void performMedicalCheckup(String animalId, Employee veterinarian, String notes);
	List<Animal> getAnimalsNeedingCheckup();
	void updateHealthStatus(String animalId, HealthStatus status, String reason);
}