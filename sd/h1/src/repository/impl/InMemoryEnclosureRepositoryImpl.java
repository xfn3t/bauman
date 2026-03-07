package repository.impl;


import model.enclosure.Enclosure;
import repository.EnclosureRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryEnclosureRepositoryImpl implements EnclosureRepository {

	private final Map<String, Enclosure> enclosures;

	public InMemoryEnclosureRepositoryImpl() {
		this.enclosures = new ConcurrentHashMap<>();
	}

	@Override
	public void save(Enclosure enclosure) {
		enclosures.put(enclosure.getId(), enclosure);
	}

	@Override
	public Optional<Enclosure> findById(String id) {
		return Optional.ofNullable(enclosures.get(id));
	}

	@Override
	public List<Enclosure> findAll() {
		return new ArrayList<>(enclosures.values());
	}

	@Override
	public void update(Enclosure enclosure) {
		if (enclosures.containsKey(enclosure.getId())) {
			enclosures.put(enclosure.getId(), enclosure);
		}
	}

	@Override
	public void delete(String id) {
		enclosures.remove(id);
	}
}