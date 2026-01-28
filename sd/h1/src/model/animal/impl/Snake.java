package model.animal.impl;

import model.animal.Animal;
import model.animal.Species;

import java.time.LocalDateTime;

public class Snake extends Animal {

	private static final String FEEDING_INSTRUCTIONS = "Грызуны 1 раз в неделю";
	private static final String CLEANING_INSTRUCTIONS = "Контроль температуры, уборка после линьки";

	public Snake(String id, String name) {
		super(id, name, Species.SNAKE);
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
				getLastFeedingTime().plusHours(10);
	}

	@Override
	public LocalDateTime getNextCleaningTime() {
		return getLastCleaningTime() == null ?
				LocalDateTime.now() :
				getLastCleaningTime().plusDays(5);
	}
}