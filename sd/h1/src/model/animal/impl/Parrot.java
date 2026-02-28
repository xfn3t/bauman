package model.animal.impl;

import model.animal.Animal;
import model.animal.Species;

import java.time.LocalDateTime;

public class Parrot extends Animal {

	private static final String FEEDING_INSTRUCTIONS = "Зерновая смесь, фрукты, 200г в день";
	private static final String CLEANING_INSTRUCTIONS = "Смена воды ежедневно, уборка клетки раз в 2 дня";

	public Parrot(String id, String name) {
		super(id, name, Species.PARROT);
	}

	@Override
	public String getFeedingInstructions() {
		return FEEDING_INSTRUCTIONS;
	}

	@Override
	public String getCleaningInstructions() {
		return CLEANING_INSTRUCTIONS;
	}

	@Override
	public LocalDateTime getNextFeedingTime() {
		return getLastFeedingTime() == null ?
				LocalDateTime.now() :
				getLastFeedingTime().plusHours(4);
	}

	@Override
	public LocalDateTime getNextCleaningTime() {
		return getLastCleaningTime() == null ?
				LocalDateTime.now() :
				getLastCleaningTime().plusDays(1);
	}

	public String talk() {
		return "Пррривет";
	}
}