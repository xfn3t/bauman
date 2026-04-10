package repository.impl;

import model.event.Exhibition;
import repository.ExhibitionRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryExhibitionRepositoryImpl implements ExhibitionRepository {

	private final Map<String, Exhibition> exhibitions;

	public InMemoryExhibitionRepositoryImpl() {
		this.exhibitions = new ConcurrentHashMap<>();
	}

	@Override
	public void save(Exhibition exhibition) {
		exhibitions.put(exhibition.getId(), exhibition);
	}

	@Override
	public Optional<Exhibition> findById(String id) {
		return Optional.ofNullable(exhibitions.get(id));
	}

	@Override
	public List<Exhibition> findAll() {
		return new ArrayList<>(exhibitions.values());
	}

	@Override
	public List<Exhibition> findByDate(LocalDateTime date) {
		List<Exhibition> result = new ArrayList<>();
		LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1);

		for (Exhibition exhibition : exhibitions.values()) {
			LocalDateTime startTime = exhibition.getStartTime();
			if (!startTime.isBefore(startOfDay) && startTime.isBefore(endOfDay)) {
				result.add(exhibition);
			}
		}
		return result;
	}

	@Override
	public List<Exhibition> findUpcoming(LocalDateTime from) {
		List<Exhibition> result = new ArrayList<>();
		for (Exhibition exhibition : exhibitions.values()) {
			if (exhibition.getStartTime().isAfter(from)) {
				result.add(exhibition);
			}
		}
		result.sort(Comparator.comparing(Exhibition::getStartTime));
		return result;
	}

	@Override
	public void delete(String id) {
		exhibitions.remove(id);
	}
}