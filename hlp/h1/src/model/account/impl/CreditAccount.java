package model.account.impl;

import model.Customer;
import model.account.Account;
import model.account.AccountType;

public class CreditAccount extends Account {

	private final double creditLimit;

	public CreditAccount(String accountNumber, Customer owner, double creditLimit) {
		super(accountNumber, owner);
		this.creditLimit = creditLimit;
	}

	@Override
	public boolean withdraw(double amount) {
		if (getBalance() - amount < -creditLimit) {
			log.error("Сумма превышает кредитный лимит");
			return false;
		}
		setBalance(getBalance() - amount);
		return true;
	}

	@Override
	protected String getExtraInfo() {
		return String.format(", Тип счета: %s, Кредитный лимит: %.2f", AccountType.CREDIT, creditLimit);
	}

}
