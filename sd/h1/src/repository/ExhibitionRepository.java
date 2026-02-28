package repository;

import model.event.Exhibition;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExhibitionRepository {
	void save(Exhibition exhibition);
	Optional<Exhibition> findById(String id);
	List<Exhibition> findAll();
	List<Exhibition> findByDate(LocalDateTime date);
	List<Exhibition> findUpcoming(LocalDateTime from);
	void delete(String id);
}
