package model.employee;

public abstract class Employee {

	private final String id;
	private final String name;
	private final String email;
	private final Role role;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public Role getRole() {
		return role;
	}

	public Employee(String id, String name, String email, Role role) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.role = role;
	}

	public abstract String performDuties();

}