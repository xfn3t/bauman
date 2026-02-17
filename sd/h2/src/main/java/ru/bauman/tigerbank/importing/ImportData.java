package ru.bauman.tigerbank.importing;

import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.operation.dto.OperationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportData {
	private List<BankAccountDto> accounts;
	private List<CategoryDto> categories;
	private List<OperationDto> operations;
}