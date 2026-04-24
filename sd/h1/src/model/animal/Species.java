package model.animal;

public enum Species {

	LION("Лев"),
	PARROT("Попугай"),
	SNAKE("Змея");

	private final String name;

	Species(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
