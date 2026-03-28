package model.account.impl;

import model.Customer;
import model.account.Account;
import model.account.AccountType;

public class DebitAccount extends Account {

	public DebitAccount(String accountNumber, Customer owner) {
		super(accountNumber, owner);
	}

	@Override
	public boolean withdraw(double amount) {
		if (getBalance() < amount) return false;
		return super.withdraw(amount);
	}

	@Override
	protected String getExtraInfo() {
		return String.format(", Тип счета: %s", AccountType.DEBIT);
	}
}
