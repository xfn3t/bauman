package ru.bauman.tigerbank.account.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.account.factory.BankAccountFactory;
import ru.bauman.tigerbank.account.service.BankAccountService;
import ru.bauman.tigerbank.account.service.balance.BalanceRecalculationService;
import ru.bauman.tigerbank.account.dto.CreateBankAccountRequest;
import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BankAccountFacade {

    private final BankAccountService bankAccountService;
    private final BalanceRecalculationService balanceRecalculationService;
    private final BankAccountFactory bankAccountFactory;

    public BankAccountDto create(CreateBankAccountRequest request) {
        return bankAccountService.create(bankAccountFactory.create(request));
    }

    public BankAccountDto update(Long id, BankAccountDto dto) {
        return bankAccountService.update(id, dto);
    }

    public void delete(Long id) {
        bankAccountService.delete(id);
    }

    public BankAccountDto findById(Long id) {
        return bankAccountService.findById(id);
    }

    public List<BankAccountDto> findAll() {
        return bankAccountService.findAll();
    }

    public void autoRecalcBalance(Long accountId) {
        balanceRecalculationService.autoRecalc(accountId);
    }

    public void manualRecalcBalance(Long accountId, BigDecimal correctBalance) {
        balanceRecalculationService.manualRecalc(accountId, correctBalance);
    }
}