package service.impl;

import exception.NotFoundException;
import model.animal.Animal;
import model.animal.Species;
import model.enclosure.Enclosure;
import repository.AnimalRepository;
import service.AnimalService;

import java.util.List;

public class AnimalServiceImpl implements AnimalService {

	private final AnimalRepository animalRepository;

	public AnimalServiceImpl(AnimalRepository animalRepository) {
		this.animalRepository = animalRepository;
	}

	@Override
	public void registerAnimal(Animal animal, Enclosure enclosure) {
		animal.setEnclosure(enclosure);
		animalRepository.save(animal);
		System.out.printf("Зарегистрировано новое животное: %s (%s)%n",
				animal.getName(), animal.getSpeciesName());
	}

	@Override
	public Animal findAnimal(String id) {
		return animalRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Животное не найдено: " + id));
	}

	@Override
	public List<Animal> getAllAnimals() {
		return animalRepository.findAll();
	}

	@Override
	public List<Animal> getAnimalsBySpecies(Species species) {
		return animalRepository.findBySpecies(species);
	}

	@Override
	public long getTotalAnimals() {
		return animalRepository.count();
	}
}