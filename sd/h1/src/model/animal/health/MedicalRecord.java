package model.animal.health;

import java.time.LocalDate;

public class MedicalRecord {

	private final LocalDate date;
	private final HealthStatus healthStatus;
	private final String notes;
	private final String veterinarian;

	public MedicalRecord(LocalDate date, HealthStatus healthStatus, String notes, String veterinarian) {
		this.date = date;
		this.healthStatus = healthStatus;
		this.notes = notes;
		this.veterinarian = veterinarian;
	}

	public LocalDate getDate() { return date; }
	public HealthStatus getHealthStatus() { return healthStatus; }
	public String getNotes() { return notes; }
	public String getVeterinarian() { return veterinarian; }
}