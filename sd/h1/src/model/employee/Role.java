package model.employee;

public enum Role {

	ZOOKEEPER("Смотритель"),
	VETERINARIAN("Ветеринар"),
	ADMINISTRATOR("Администратор");

	private final String description;

	Role(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}