package repository;

import model.animal.Animal;
import model.animal.Species;

import java.util.List;
import java.util.Optional;

public interface AnimalRepository {
	void save(Animal animal);
	Optional<Animal> findById(String id);
	List<Animal> findAll();
	List<Animal> findBySpecies(Species species);
	void delete(String id);
	long count();
}