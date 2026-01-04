package model.event;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Exhibition {

	private final String id;
	private final String name;
	private final LocalDateTime startTime;
	private final int durationMinutes;
	private final Set<String> participantIds;

	public Exhibition(String id, String name, LocalDateTime startTime, int durationMinutes) {
		this.id = id;
		this.name = name;
		this.startTime = startTime;
		this.durationMinutes = durationMinutes;
		this.participantIds = new HashSet<>();
	}

	public void addParticipant(String animalId) {
		participantIds.add(animalId);
	}

	public void removeParticipant(String animalId) {
		participantIds.remove(animalId);
	}

	public boolean hasParticipant(String animalId) {
		return participantIds.contains(animalId);
	}

	public int getParticipantCount() {
		return participantIds.size();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public int getDurationMinutes() {
		return durationMinutes;
	}

	public Set<String> getParticipantIds() {
		return new HashSet<>(participantIds);
	}
}
