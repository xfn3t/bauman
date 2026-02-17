package ru.bauman.tigerbank.importing.impl;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.category.dto.CategoryTypeDto;
import ru.bauman.tigerbank.importing.ImportData;
import ru.bauman.tigerbank.importing.Importer;
import ru.bauman.tigerbank.operation.dto.OperationDto;
import ru.bauman.tigerbank.operation.dto.OperationTypeDto;
import ru.bauman.tigerbank.export.ExportFormat;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvImporter implements Importer {

	@Override
	public ImportData importData(InputStream input) throws Exception {
		List<BankAccountDto> accounts = new ArrayList<>();
		List<CategoryDto> categories = new ArrayList<>();
		List<OperationDto> operations = new ArrayList<>();

		try (CSVReader reader = new CSVReader(new InputStreamReader(input))) {
			List<String[]> lines = reader.readAll();
			int i = 0;
			while (i < lines.size()) {
				String[] line = lines.get(i);
				if (line.length == 0) {
					i++;
					continue;
				}
				if (line[0].equals("=== Bank Accounts ===")) {
					i += 2; // пропускаем заголовки
					while (i < lines.size() && lines.get(i).length > 0 && !lines.get(i)[0].startsWith("===")) {
						String[] data = lines.get(i);
						accounts.add(new BankAccountDto(
								Long.parseLong(data[0]),
								data[1],
								new BigDecimal(data[2])
						));
						i++;
					}
				} else if (line[0].equals("=== Categories ===")) {
					i += 2;
					while (i < lines.size() && lines.get(i).length > 0 && !lines.get(i)[0].startsWith("===")) {
						String[] data = lines.get(i);
						CategoryTypeDto typeDto = new CategoryTypeDto(
								Long.parseLong(data[2]),
								data[3]
						);
						categories.add(new CategoryDto(
								Long.parseLong(data[0]),
								data[1],
								typeDto
						));
						i++;
					}
				} else if (line[0].equals("=== Operations ===")) {
					i += 2;
					while (i < lines.size() && lines.get(i).length > 0 && !lines.get(i)[0].startsWith("===")) {
						String[] data = lines.get(i);
						OperationTypeDto typeDto = new OperationTypeDto(Long.parseLong(data[4]), null);
						BankAccountDto accountDto = new BankAccountDto(Long.parseLong(data[5]), null, null);
						CategoryDto categoryDto = new CategoryDto(Long.parseLong(data[6]), null, null);
						operations.add(new OperationDto(
								Long.parseLong(data[0]),
								new BigDecimal(data[1]),
								LocalDateTime.parse(data[2]),
								data[3],
								typeDto,
								accountDto,
								categoryDto
						));
						i++;
					}
				} else {
					i++;
				}
			}
		} catch (CsvException e) {
			throw new RuntimeException("Failed to parse CSV", e);
		}

		return new ImportData(accounts, categories, operations);
	}

	@Override
	public ExportFormat getFormat() {
		return ExportFormat.CSV;
	}
}