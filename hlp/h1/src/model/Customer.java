package model;

public class Customer {

	private final int id;
	private final String fullName;

	public Customer(int id, String fullName) {
		this.id = id;
		this.fullName = fullName;
	}

	public String getFullName() {
		return fullName;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return id + ". '" + fullName;
	}
}
