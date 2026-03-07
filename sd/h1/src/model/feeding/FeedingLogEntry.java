package model.feeding;

import java.time.LocalDateTime;

public class FeedingLogEntry {

	private final String animalId;
	private final LocalDateTime feedingTime;
	private final String employeeName;

	public FeedingLogEntry(String animalId, LocalDateTime feedingTime, String employeeName) {
		this.animalId = animalId;
		this.feedingTime = feedingTime;
		this.employeeName = employeeName;
	}

	public String getAnimalId() {
		return animalId;
	}

	public LocalDateTime getFeedingTime() {
		return feedingTime;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	@Override
	public String toString() {
		return String.format("[%s] %s покормил(а) животное %s",
				feedingTime.toLocalTime(), employeeName, animalId);
	}
}