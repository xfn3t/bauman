package repository;

import model.feeding.FeedingLogEntry;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FeedingLogRepository {
	void logFeeding(String animalId, LocalDateTime feedingTime, String employeeName);
	Optional<LocalDateTime> getLastFeedingTime(String animalId);
	List<FeedingLogEntry> getFeedingLogForAnimal(String animalId);
	List<FeedingLogEntry> getFeedingLogForDay(LocalDateTime date);
	List<FeedingLogEntry> getAllFeedingLogs();
}
