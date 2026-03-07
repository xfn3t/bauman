package repository.impl;

import model.feeding.FeedingLogEntry;
import repository.FeedingLogRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryFeedingLogRepositoryImpl implements FeedingLogRepository {

	private final Map<String, List<FeedingLogEntry>> feedingLogs;

	public InMemoryFeedingLogRepositoryImpl() {
		this.feedingLogs = new ConcurrentHashMap<>();
	}

	@Override
	public void logFeeding(String animalId, LocalDateTime feedingTime, String employeeName) {
		FeedingLogEntry entry = new FeedingLogEntry(animalId, feedingTime, employeeName);
		feedingLogs.computeIfAbsent(animalId, k -> new ArrayList<>()).add(entry);
	}

	@Override
	public Optional<LocalDateTime> getLastFeedingTime(String animalId) {
		List<FeedingLogEntry> entries = feedingLogs.get(animalId);
		if (entries == null || entries.isEmpty()) {
			return Optional.empty();
		}
		return entries.stream()
				.map(FeedingLogEntry::getFeedingTime)
				.max(LocalDateTime::compareTo);
	}

	@Override
	public List<FeedingLogEntry> getFeedingLogForAnimal(String animalId) {
		List<FeedingLogEntry> entries = feedingLogs.get(animalId);
		return entries != null ? new ArrayList<>(entries) : new ArrayList<>();
	}

	@Override
	public List<FeedingLogEntry> getFeedingLogForDay(LocalDateTime date) {
		LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1);

		List<FeedingLogEntry> result = new ArrayList<>();
		for (List<FeedingLogEntry> entries : feedingLogs.values()) {
			for (FeedingLogEntry entry : entries) {
				if (!entry.getFeedingTime().isBefore(startOfDay)
						&& entry.getFeedingTime().isBefore(endOfDay)) {
					result.add(entry);
				}
			}
		}
		result.sort(Comparator.comparing(FeedingLogEntry::getFeedingTime));
		return result;
	}

	@Override
	public List<FeedingLogEntry> getAllFeedingLogs() {
		List<FeedingLogEntry> result = new ArrayList<>();
		for (List<FeedingLogEntry> entries : feedingLogs.values()) {
			result.addAll(entries);
		}
		result.sort(Comparator.comparing(FeedingLogEntry::getFeedingTime).reversed());
		return result;
	}
}