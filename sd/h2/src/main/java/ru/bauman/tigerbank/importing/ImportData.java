package ru.bauman.tigerbank.importing;

import lombok.*;
import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.operation.dto.OperationDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportData {
	private List<BankAccountDto> accounts;
	private List<CategoryDto> categories;
	private List<OperationDto> operations;
}