package bank.console;

import bank.BankService;
import model.Customer;
import model.account.Account;

import java.util.Scanner;

public class BankConsoleMenu {
	private final BankService bank;
	private final Scanner scanner;

	public BankConsoleMenu(BankService bank) {
		this.bank = bank;
		this.scanner = new Scanner(System.in);
	}

	public void run() {
		while (true) {
			printMenu();
			int choice = readInt("Выберите действие: ");

			switch (choice) {
				case 1 -> createCustomer();
				case 2 -> openDebitAccount();
				case 3 -> openCreditAccount();
				case 4 -> deposit();
				case 5 -> withdraw();
				case 6 -> transfer();
				case 7 -> printCustomerAccounts();
				case 8 -> printTransactions();
				case 9 -> printReport();
				case 10 -> {
					System.out.println("Выход из программы.");
					return;
				}
				default -> System.out.println("Неверный выбор. Попробуйте снова.");
			}

			System.out.println("\nНажмите Enter для продолжения...");
			scanner.nextLine();
		}
	}

	private void printMenu() {
		System.out.println("\n" + "=".repeat(5) + "Банковская система" + "=".repeat(5));
		System.out.println("1. Создать клиента");
		System.out.println("2. Открыть дебетовый счет");
		System.out.println("3. Открыть кредитный счет");
		System.out.println("4. Пополнить");
		System.out.println("5. Снять");
		System.out.println("6. Перевести");
		System.out.println("7. Показать счета клиента");
		System.out.println("8. Показать транзакции");
		System.out.println("9. Отчет банка");
		System.out.println("10. Выход");
	}

	private void createCustomer() {
		System.out.print("Введите ФИО клиента: ");
		String fullName = scanner.nextLine();

		Customer customer = bank.createCustomer(fullName);
		System.out.println("Клиент создан. ID: " + customer.getId() + ", ФИО: " + customer.getFullName());
	}

	private void openDebitAccount() {
		try {
			int customerId = readInt("Введите ID клиента: ");
			Customer customer = bank.findCustomerById(customerId);

			Account account = bank.openDebitAccount(customer);
			System.out.println("Дебетовый счет открыт.");
			System.out.println("Номер счета: " + account.getAccountNumber());
			System.out.println("Владелец: " + customer.getFullName());
			System.out.println("Баланс: " + account.getBalance());
		} catch (RuntimeException e) {
			System.out.println("Ошибка: " + e.getMessage());
		}
	}

	private void openCreditAccount() {
		try {
			int customerId = readInt("Введите ID клиента: ");
			Customer customer = bank.findCustomerById(customerId);

			double creditLimit = readDouble("Введите кредитный лимит: ");

			Account account = bank.openCreditAccount(customer, creditLimit);
			System.out.println("Кредитный счет открыт.");
			System.out.println("Номер счета: " + account.getAccountNumber());
			System.out.println("Владелец: " + customer.getFullName());
			System.out.println("Кредитный лимит: " + creditLimit);
			System.out.println("Баланс: " + account.getBalance());
		} catch (RuntimeException e) {
			System.out.println("Ошибка: " + e.getMessage());
		}
	}

	private void deposit() {
		System.out.print("Введите номер счета для пополнения: ");
		String accountNumber = scanner.nextLine();
		double amount = readDouble("Введите сумму для пополнения: ");

		boolean success = bank.deposit(accountNumber, amount);

		if (success) {
			System.out.println("Пополнение успешно.");
			System.out.println("Сумма: " + amount);
		} else {
			System.out.println("Ошибка при пополнении счета.");
		}
	}

	private void withdraw() {
		System.out.print("Введите номер счета для снятия: ");
		String accountNumber = scanner.nextLine();
		double amount = readDouble("Введите сумму для снятия: ");

		boolean success = bank.withdraw(accountNumber, amount);

		if (success) {
			System.out.println("Снятие успешно.");
			System.out.println("Сумма: " + amount);
		} else {
			System.out.println("Ошибка при снятии средств.");
		}
	}

	private void transfer() {
		System.out.print("Введите номер счета отправителя: ");
		String fromAccount = scanner.nextLine();
		System.out.print("Введите номер счета получателя: ");
		String toAccount = scanner.nextLine();
		double amount = readDouble("Введите сумму для перевода: ");

		boolean success = bank.transfer(fromAccount, toAccount, amount);

		if (success) {
			System.out.println("Перевод успешно выполнен.");
			System.out.println("Сумма: " + amount);
			System.out.println("От: " + fromAccount);
			System.out.println("Кому: " + toAccount);
		} else {
			System.out.println("Ошибка при переводе средств.");
		}
	}

	private void printCustomerAccounts() {
		try {
			int customerId = readInt("Введите ID клиента: ");
			bank.findCustomerById(customerId);
			System.out.println("\nСчета клиента:");
			bank.printCustomerAccounts(customerId);
		} catch (RuntimeException e) {
			System.out.println("Ошибка: " + e.getMessage());
		}
	}

	private void printTransactions() {
		System.out.println("\nИстория транзакций:");
		bank.printTransactions();
	}

	private void printReport() {
		System.out.println("\nОтчет по банку:");
		bank.printReport();
	}

	private int readInt(String message) {
		while (true) {
			try {
				System.out.print(message);
				String input = scanner.nextLine();
				return Integer.parseInt(input);
			} catch (NumberFormatException e) {
				System.out.println("Ошибка: введите целое число.");
			}
		}
	}

	private double readDouble(String message) {
		while (true) {
			try {
				System.out.print(message);
				String input = scanner.nextLine();
				return Double.parseDouble(input);
			} catch (NumberFormatException e) {
				System.out.println("Ошибка: введите число.");
			}
		}
	}
}