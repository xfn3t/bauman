package ru.bauman.tigerbank.export;

import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.operation.dto.OperationDto;
import java.io.OutputStream;
import java.util.List;

public interface Exporter {
	void export(List<BankAccountDto> accounts, List<CategoryDto> categories, List<OperationDto> operations, OutputStream output) throws Exception;
	ExportFormat getFormat();
}