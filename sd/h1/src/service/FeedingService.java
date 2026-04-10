package service;


import model.animal.Animal;
import model.employee.Employee;
import model.feeding.FeedingLogEntry;

import java.util.List;

public interface FeedingService {
	void feedAnimal(String animalId, Employee employee);
	void feedAnimals(List<String> animalIds, Employee employee);
	boolean isAnimalFedToday(String animalId);
	List<Animal> getAnimalsNeedingFeeding();
	List<FeedingLogEntry> getTodayFeedingLog();
	List<FeedingLogEntry> getAnimalFeedingHistory(String animalId);
}