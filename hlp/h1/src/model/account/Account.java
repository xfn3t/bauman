package model.account;

import logger.Logger;
import model.Customer;

public abstract class Account {

	private String accountNumber;
	private double balance;
	private Customer owner;

	protected final Logger log = Logger.getInstance();

	public Account(String accountNumber, Customer owner) {
		this.accountNumber = accountNumber;
		this.balance = 0;
		this.owner = owner;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public Customer getOwner() {
		return owner;
	}

	public void setOwner(Customer owner) {
		this.owner = owner;
	}

	public boolean deposit(double amount) {

		if (amount < 1) {
			log.error("Депозит не может быть меньше 1");
			return false;
		}

		balance += amount;
		return true;
	}

	public boolean withdraw(double amount) {
		if (amount > balance) {
			log.error("Недостаточно средств");
			return false;
		}

		balance -= amount;
		return true;
	}

	public boolean transfer(Account to, double amount) {

		if (amount < 1) {
			log.error("Сумма должна быть больше 0");
			return false;
		}

		if (!withdraw(amount)) {
			log.error("Недостаточно средств для перевода");
			return false;
		}

		to.deposit(amount);

		return true;
	}

	protected String getExtraInfo() {
		return "";
	}

	@Override
	public String toString() {
		return String.format("Счет: %s, Баланс: %.2f%s",
				accountNumber,
				balance,
				getExtraInfo());
	}
}
