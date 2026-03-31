package ru.bauman.tigerbank.account.repository;

import ru.bauman.tigerbank.account.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
}