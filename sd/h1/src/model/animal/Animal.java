package model.animal;

import model.animal.health.HealthStatus;
import model.animal.health.MedicalRecord;
import model.enclosure.Enclosure;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Animal {

	private final String id;
	private final String name;
	private final Species species;
	private final LocalDate arrivalDate;
	private Enclosure enclosure;
	private HealthStatus healthStatus;
	private final List<MedicalRecord> medicalRecords;
	private LocalDateTime lastFeedingTime;
	private LocalDateTime lastCleaningTime;

	public Animal(String id, String name, Species species) {
		this.id = id;
		this.name = name;
		this.species = species;
		this.arrivalDate = LocalDate.now();
		this.healthStatus = HealthStatus.HEALTHY;
		this.medicalRecords = new ArrayList<>();
	}

	public abstract String getFeedingInstructions();
	public abstract String getCleaningInstructions();
	public abstract LocalDateTime getNextFeedingTime();
	public abstract LocalDateTime getNextCleaningTime();

	public boolean isHungry() {
		return lastFeedingTime == null ||
				LocalDateTime.now().isAfter(getNextFeedingTime());
	}

	public boolean needsCleaning() {
		return lastCleaningTime == null ||
				LocalDateTime.now().isAfter(getNextCleaningTime());
	}

	public void addMedicalRecord(MedicalRecord record) {
		medicalRecords.add(record);
		updateHealthStatus(record.getHealthStatus());
	}

	private void updateHealthStatus(HealthStatus newStatus) {
		if (newStatus.getPriority() > this.healthStatus.getPriority()) {
			this.healthStatus = newStatus;
		}
	}

	public void feed() {
		this.lastFeedingTime = LocalDateTime.now();
	}

	public void clean() {
		this.lastCleaningTime = LocalDateTime.now();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Species getSpecies() {
		return species;
	}

	public String getSpeciesName() {
		return species.getName();
	}

	public LocalDate getArrivalDate() {
		return arrivalDate;
	}

	public Enclosure getEnclosure() {
		return enclosure;
	}

	public HealthStatus getHealthStatus() {
		return healthStatus;
	}

	public List<MedicalRecord> getMedicalRecords() {
		return new ArrayList<>(medicalRecords);
	}

	public LocalDateTime getLastFeedingTime() {
		return lastFeedingTime;
	}

	public LocalDateTime getLastCleaningTime() {
		return lastCleaningTime;
	}

	public void setEnclosure(Enclosure enclosure) {
		this.enclosure = enclosure;
	}
}