package model.animal.health;

public enum HealthStatus {

	CRITICAL(4, "Критическое"),
	SICK(3, "Болен"),
	RECOVERING(2, "Выздоравливает"),
	HEALTHY(1, "Здоров");

	private final int priority;
	private final String description;

	HealthStatus(int priority, String description) {
		this.priority = priority;
		this.description = description;
	}

	public int getPriority() {
		return priority;
	}

	public String getDescription() {
		return description;
	}
}