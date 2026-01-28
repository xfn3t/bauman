package model.enclosure;

import java.time.LocalDateTime;

public class Enclosure {

	private final String id;
	private final String name;
	private final double area;
	private LocalDateTime lastCleaningTime;
	private String dirtyReason;
	private boolean needsCleaning;

	public Enclosure(String id, String name, double area) {
		this.id = id;
		this.name = name;
		this.area = area;
		this.needsCleaning = true;
	}

	public void clean() {
		this.lastCleaningTime = LocalDateTime.now();
		this.dirtyReason = null;
		this.needsCleaning = false;
	}

	public void markAsDirty(String reason) {
		this.dirtyReason = reason;
		this.needsCleaning = true;
	}

	public boolean needsCleaning() {
		if (needsCleaning) return true;
		if (lastCleaningTime == null) return true;
		return lastCleaningTime.plusDays(1).isBefore(LocalDateTime.now());
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getArea() {
		return area;
	}

	public LocalDateTime getLastCleaningTime() {
		return lastCleaningTime;
	}

	public String getDirtyReason() {
		return dirtyReason;
	}

	public boolean isNeedsCleaning() {
		return needsCleaning;
	}
}