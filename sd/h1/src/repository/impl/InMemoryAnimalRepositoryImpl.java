package repository.impl;

import model.animal.Animal;
import model.animal.Species;
import repository.AnimalRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAnimalRepositoryImpl implements AnimalRepository {

	private final Map<String, Animal> animals;

	public InMemoryAnimalRepositoryImpl() {
		this.animals = new ConcurrentHashMap<>();
	}

	@Override
	public void save(Animal animal) {
		animals.put(animal.getId(), animal);
	}

	@Override
	public Optional<Animal> findById(String id) {
		return Optional.ofNullable(animals.get(id));
	}

	@Override
	public List<Animal> findAll() {
		return new ArrayList<>(animals.values());
	}

	@Override
	public List<Animal> findBySpecies(Species species) {
		List<Animal> result = new ArrayList<>();
		for (Animal animal : animals.values()) {
			if (animal.getSpecies().equals(species)) {
				result.add(animal);
			}
		}
		return result;
	}

	@Override
	public void delete(String id) {
		animals.remove(id);
	}

	@Override
	public long count() {
		return animals.size();
	}
}