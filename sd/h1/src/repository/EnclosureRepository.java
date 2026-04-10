package repository;

import model.enclosure.Enclosure;

import java.util.List;
import java.util.Optional;

public interface EnclosureRepository {
	void save(Enclosure enclosure);
	Optional<Enclosure> findById(String id);
	List<Enclosure> findAll();
	void update(Enclosure enclosure);
	void delete(String id);
}
