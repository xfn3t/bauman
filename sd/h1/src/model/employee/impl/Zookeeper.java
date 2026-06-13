package model.employee.impl;

import model.employee.Employee;
import model.employee.Role;

public class Zookeeper extends Employee {

	public Zookeeper(String id, String name, String email) {
		super(id, name, email, Role.ZOOKEEPER);
	}

	@Override
	public String performDuties() {
		return "Уход за животными, кормление, уборка вольеров";
	}

	public String feedAnimals() {
		return "Кормлю животных согласно расписанию";
	}
}