package model.animal.impl;

import model.animal.Animal;
import model.animal.Species;

import java.time.LocalDateTime;

public class Lion extends Animal {

	private static final String FEEDING_INSTRUCTIONS = "Мясо, 5 кг в день, кормить утром и вечером";
	private static final String CLEANING_INSTRUCTIONS = "Ежедневная уборка вольера, дезинфекция раз в неделю";

	public Lion(String id, String name) {
		super(id, name, Species.LION);
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
				getLastFeedingTime().plusHours(12); // Кормить каждые 12 часов
	}

	@Override
	public LocalDateTime getNextCleaningTime() {
		return getLastCleaningTime() == null ?
				LocalDateTime.now() :
				getLastCleaningTime().plusDays(1); // Убирать каждый день
	}

	public String roar() {
		return "Ррррр";
	}
}