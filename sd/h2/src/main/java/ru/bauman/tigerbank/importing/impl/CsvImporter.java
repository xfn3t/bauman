package ru.bauman.tigerbank.importing.impl;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.category.dto.CategoryTypeDto;
import ru.bauman.tigerbank.export.ExportFormat;
import ru.bauman.tigerbank.importing.dto.ImportData;
import ru.bauman.tigerbank.importing.Importer;
import ru.bauman.tigerbank.operation.dto.OperationDto;
import ru.bauman.tigerbank.operation.dto.OperationTypeDto;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
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

				// skip empty strings
				if (line.length == 0 || (line.length >= 1 && (line[0] == null || line[0].trim().isEmpty()))) {
					i++;
					continue;
				}

				switch (line[0].trim()) {
					case "=== Bank Accounts ===" -> {
						i += 2; // skip headers
						while (i < lines.size() && lines.get(i).length > 0 && !lines.get(i)[0].startsWith("===")) {
							String[] data = lines.get(i);
							if (data.length < 3 || isAnyEmpty(data[0], data[1], data[2])) {
								log.warn("Skip bad for calculate string: {}", (Object) data);
								i++;
								continue;
							}
							try {
								accounts.add(new BankAccountDto(
										Long.parseLong(data[0].trim()),
										data[1].trim(),
										new BigDecimal(data[2].trim())
								));
							} catch (NumberFormatException e) {
								log.warn("Parsing exception account: {}", data, e);
							}
							i++;
						}
					}
					case "=== Categories ===" -> {
						i += 2;
						while (i < lines.size() && lines.get(i).length > 0 && !lines.get(i)[0].startsWith("===")) {
							String[] data = lines.get(i);
							if (data.length < 4 || isAnyEmpty(data[0], data[1], data[2], data[3])) {
								log.warn("Skip bad string category: {}", (Object) data);
								i++;
								continue;
							}
							try {
								CategoryTypeDto typeDto = new CategoryTypeDto(
										Long.parseLong(data[2].trim()),
										data[3].trim()
								);
								categories.add(new CategoryDto(
										Long.parseLong(data[0].trim()),
										data[1].trim(),
										typeDto
								));
							} catch (NumberFormatException e) {
								log.warn("Parsing exception category: {}", data, e);
							}
							i++;
						}
					}
					case "=== Operations ===" -> {
						i += 2;
						while (i < lines.size() && lines.get(i).length > 0 && !lines.get(i)[0].startsWith("===")) {
							String[] data = lines.get(i);
							if (data.length < 7 || isAnyEmpty(data[0], data[1], data[2], data[4], data[5], data[6])) {
								log.warn("Skip bad string category operation: {}", (Object) data);
								i++;
								continue;
							}
							try {
								OperationTypeDto typeDto = new OperationTypeDto(Long.parseLong(data[4].trim()), null);
								BankAccountDto accountDto = new BankAccountDto(Long.parseLong(data[5].trim()), null, null);
								CategoryDto categoryDto = new CategoryDto(Long.parseLong(data[6].trim()), null, null);
								operations.add(new OperationDto(
										Long.parseLong(data[0].trim()),
										new BigDecimal(data[1].trim()),
										LocalDateTime.parse(data[2].trim()),
										data.length > 3 ? data[3] : "",
										typeDto,
										accountDto,
										categoryDto
								));
							} catch (Exception e) {
								log.warn("Parsing exception category operation: {}", data, e);
							}
							i++;
						}
					}
					default -> i++;
				}
			}
		} catch (CsvException e) {
			throw new RuntimeException("Failed to parse CSV", e);
		}

		return new ImportData(accounts, categories, operations);
	}

	private boolean isAnyEmpty(String... values) {
		for (String v : values) {
			if (v == null || v.trim().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ExportFormat getFormat() {
		return ExportFormat.CSV;
	}
}