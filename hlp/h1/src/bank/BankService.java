package bank;

import model.Customer;
import model.account.Account;

public interface BankService {

	Customer createCustomer(String fullName);
	Customer findCustomerById(int customerId);

	Account openDebitAccount(Customer owner);
	Account openCreditAccount(Customer owner, double creditLimit);
	Account findAccount(String accountNumber);

	boolean deposit(String accountNumber, double amount);
	boolean withdraw(String accountNumber, double amount);
	boolean transfer(String from, String to, double amount);

	void printCustomerAccounts(int customerId);
	void printTransactions();
	void printReport();
}