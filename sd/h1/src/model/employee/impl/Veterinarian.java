package model.employee.impl;


import model.employee.Employee;
import model.employee.Role;

public class Veterinarian extends Employee {

	public Veterinarian(String id, String name, String email) {
		super(id, name, email, Role.VETERINARIAN);
	}

	@Override
	public String performDuties() {
		return "Проведение медосмотров, лечение животных, вакцинация";
	}

	public String conductCheckup() {
		return "Провожу медицинский осмотр животных";
	}
}