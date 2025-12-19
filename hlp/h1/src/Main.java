import bank.BankService;
import bank.impl.Bank;
import bank.console.BankConsoleMenu;

public class Main {
	public static void main(String[] args) {
		BankService bank = new Bank();
		BankConsoleMenu menu = new BankConsoleMenu(bank);
		menu.run();
	}
}