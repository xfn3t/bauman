package service;

import model.animal.Animal;
import model.animal.Species;
import model.enclosure.Enclosure;

import java.util.List;

public interface AnimalService {
	void registerAnimal(Animal animal, Enclosure enclosure);
	Animal findAnimal(String id);
	List<Animal> getAllAnimals();
	List<Animal> getAnimalsBySpecies(Species species);
	long getTotalAnimals();
}