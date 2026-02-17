package ru.bauman.tigerbank.export.impl;

import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.export.ExportFormat;
import ru.bauman.tigerbank.export.Exporter;
import ru.bauman.tigerbank.operation.dto.OperationDto;
import ru.bauman.tigerbank.account.service.BankAccountServiceInterface;
import ru.bauman.tigerbank.category.service.CategoryServiceInterface;
import ru.bauman.tigerbank.operation.service.OperationServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportService {
	private final List<Exporter> exporters;
	private final BankAccountServiceInterface accountService;
	private final CategoryServiceInterface categoryService;
	private final OperationServiceInterface operationService;

	public void exportData(ExportFormat format, String filePath) throws Exception {
		Exporter exporter = findExporter(format);
		List<BankAccountDto> accounts = accountService.findAll();
		List<CategoryDto> categories = categoryService.findAll();
		List<OperationDto> operations = operationService.findAll();

		try (OutputStream os = new FileOutputStream(filePath)) {
			exporter.export(accounts, categories, operations, os);
		}
	}

	private Exporter findExporter(ExportFormat format) {
		Map<ExportFormat, Exporter> map = exporters.stream()
				.collect(Collectors.toMap(Exporter::getFormat, Function.identity()));
		Exporter exporter = map.get(format);
		if (exporter == null) {
			throw new RuntimeException("Unsupported format: " + format);
		}
		return exporter;
	}
}