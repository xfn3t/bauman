package bank.impl;

import bank.BankService;
import logger.Logger;
import model.account.Account;
import model.account.impl.CreditAccount;
import model.Customer;
import model.account.impl.DebitAccount;
import transaction.Transaction;
import transaction.TransactionType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Bank implements BankService {

	private final Logger log = Logger.getInstance();

	private static int ID_COUNTER = 0;

	private final List<Customer> customers = new ArrayList<>();
	private final List<Account> accounts = new ArrayList<>();
	private final List<Transaction> transactions = new ArrayList<>();

	@Override
	public Customer createCustomer(String fullName) {
		++ID_COUNTER;
		Customer customer = new Customer(ID_COUNTER, fullName);
		customers.add(customer);
		return customer;
	}

	@Override
	public Customer findCustomerById(int customerId) {
		return customers.stream()
				.filter(customer -> customer.getId() == customerId)
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Customer not found"));
	}

	@Override
	public Account openDebitAccount(Customer owner) {
		Account account = new DebitAccount(generateAccountNumber(owner), owner);
		accounts.add(account);
		return account;
	}

	@Override
	public Account openCreditAccount(Customer owner, double creditLimit) {
		Account account = new CreditAccount(generateAccountNumber(owner), owner, creditLimit);
		accounts.add(account);
		return account;
	}

	@Override
	public Account findAccount(String accountNumber) {
		return accounts.stream()
				.filter(a -> a.getAccountNumber().equals(accountNumber))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Account not found"));
	}

	@Override
	public boolean deposit(String accountNumber, double amount) {

		Transaction.Builder transaction = new Transaction.Builder()
				.amount(amount)
				.transactionType(TransactionType.DEPOSIT);

		try {
			Account account = findAccount(accountNumber);

			if (!account.deposit(amount))
				throw new RuntimeException("Пополнение не выполнено");

			transaction.toAccount(accountNumber)
					.success(true)
					.message("OK");

			return true;

		} catch (RuntimeException e) {
			transaction.success(false)
					.message(e.getMessage());

			return false;
		} finally {
			transactions.add(transaction.build());
		}
	}

	@Override
	public boolean withdraw(String accountNumber, double amount) {
		Transaction.Builder transaction = new Transaction.Builder()
				.amount(amount)
				.transactionType(TransactionType.WITHDRAW);

		try {
			Account account = findAccount(accountNumber);

			if (!account.withdraw(amount))
				throw new RuntimeException("Снятие не выполнено");

			transaction.fromAccount(accountNumber)
					.success(true)
					.message("OK");

			return true;

		} catch (RuntimeException e) {
			transaction.success(false)
					.message(e.getMessage());

			return false;
		} finally {
			transactions.add(transaction.build());
		}
	}

	@Override
	public boolean transfer(String from, String to, double amount) {

		Transaction.Builder transaction = new Transaction.Builder()
				.amount(amount)
				.transactionType(TransactionType.TRANSFER);

		try {
			Account fromAccount = findAccount(from);
			Account toAccount = findAccount(to);

			if (!fromAccount.transfer(toAccount, amount))
				throw new RuntimeException("Перевод не выполнен");

			transaction.fromAccount(from)
					.toAccount(to)
					.success(true)
					.message("OK");

			return true;

		} catch (RuntimeException e) {
			transaction.success(false)
					.message(e.getMessage());

			return false;
		} finally {
			transactions.add(transaction.build());
		}
	}



	@Override
	public void printCustomerAccounts(int customerId) {

		var customerAccounts = accounts.stream()
				.filter(account -> account.getOwner().getId() == customerId)
				.collect(Collectors.toSet());

		if (customerAccounts.isEmpty()) {
			System.out.println("У клиента с ID " + customerId + " нет счетов.");
		} else {
			customerAccounts.forEach(System.out::println);
		}
	}

	@Override
	public void printTransactions() {
		transactions.forEach(System.out::println);
	}

	@Override
	public void printReport() {

		Predicate<Account> isDebitAccount = account -> account instanceof DebitAccount;
		Predicate<Account> isCreditAccount = account -> account instanceof CreditAccount;

		long countDebit = accounts.stream().filter(isDebitAccount).count();
		long countCredit = accounts.stream().filter(isCreditAccount).count();

		double sumDebitBalance = accounts.stream().filter(isDebitAccount).mapToDouble(Account::getBalance).sum();
		double sumCreditBalance = accounts.stream().filter(isCreditAccount).mapToDouble(Account::getBalance).sum();

		long countSuccessTransfer = transactions.stream().filter(Transaction::success).count();
		long countFailTransfer = transactions.size() - countSuccessTransfer;


		System.out.println(
				"Всего дебетовых аккаунтов: " + countDebit + "\n" +
				"Всего кредитных аккаунтов: " + countCredit + "\n" +
				"Сумма дебетовых балансов: " + sumDebitBalance + "\n" +
				"Сумма кредитных балансов: " + sumCreditBalance + "\n" +
				"Всего успешных переводов: " + countSuccessTransfer + "\n" +
				"Всего безуспешных переводов: " + countFailTransfer
		);

	}

	private String generateAccountNumber(Customer owner) {
		return (owner.getFullName() + "-" + UUID.randomUUID()).toLowerCase().replace(" ", "");
	}
}
