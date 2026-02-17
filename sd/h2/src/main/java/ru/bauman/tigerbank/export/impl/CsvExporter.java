package ru.bauman.tigerbank.export.impl;

import com.opencsv.CSVWriter;
import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.export.ExportFormat;
import ru.bauman.tigerbank.export.Exporter;
import ru.bauman.tigerbank.operation.dto.OperationDto;
import org.springframework.stereotype.Component;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvExporter implements Exporter {

	@Override
	public void export(List<BankAccountDto> accounts, List<CategoryDto> categories, List<OperationDto> operations, OutputStream output) throws Exception {
		try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8))) {
			// Заголовки для счетов
			writer.writeNext(new String[]{"=== Bank Accounts ==="});
			writer.writeNext(new String[]{"ID", "Name", "Balance"});
			for (BankAccountDto acc : accounts) {
				writer.writeNext(new String[]{
						String.valueOf(acc.id()),
						acc.name(),
						acc.balance().toString()
				});
			}
			writer.writeNext(new String[]{});

			// Категории
			writer.writeNext(new String[]{"=== Categories ==="});
			writer.writeNext(new String[]{"ID", "Name", "TypeID", "TypeName"});
			for (CategoryDto cat : categories) {
				writer.writeNext(new String[]{
						String.valueOf(cat.id()),
						cat.name(),
						String.valueOf(cat.type().id()),
						cat.type().name()
				});
			}
			writer.writeNext(new String[]{});

			// Операции
			writer.writeNext(new String[]{"=== Operations ==="});
			writer.writeNext(new String[]{"ID", "Amount", "Date", "Description", "TypeID", "AccountID", "CategoryID"});
			for (OperationDto op : operations) {
				writer.writeNext(new String[]{
						String.valueOf(op.id()),
						op.amount().toString(),
						op.date().toString(),
						op.description(),
						String.valueOf(op.type().id()),
						String.valueOf(op.account().id()),
						String.valueOf(op.category().id())
				});
			}
		}
	}

	@Override
	public ExportFormat getFormat() {
		return ExportFormat.CSV;
	}
}