package ru.bauman.tigerbank.importing;

import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.operation.dto.OperationDto;
import ru.bauman.tigerbank.account.service.BankAccountServiceInterface;
import ru.bauman.tigerbank.category.service.CategoryServiceInterface;
import ru.bauman.tigerbank.operation.service.OperationServiceInterface;
import ru.bauman.tigerbank.export.ExportFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportService {

	private final List<Importer> importers;
	private final BankAccountServiceInterface accountService;
	private final CategoryServiceInterface categoryService;
	private final OperationServiceInterface operationService;

	@Transactional
	public void importData(ExportFormat format, String filePath) throws Exception {
		Importer importer = findImporter(format);
		try (InputStream is = new FileInputStream(filePath)) {
			ImportData data = importer.importData(is);
			for (BankAccountDto dto : data.getAccounts()) {
				try {
					accountService.findById(dto.id());
				} catch (RuntimeException e) {
					accountService.create(dto);
				}
			}
			for (CategoryDto dto : data.getCategories()) {
				try {
					categoryService.findById(dto.id());
				} catch (RuntimeException e) {
					categoryService.create(dto);
				}
			}
			for (OperationDto dto : data.getOperations()) {
				try {
					operationService.findById(dto.id());
				} catch (RuntimeException e) {
					operationService.create(dto);
				}
			}
		}
	}

	private Importer findImporter(ExportFormat format) {
		Map<ExportFormat, Importer> map = importers.stream()
				.collect(Collectors.toMap(Importer::getFormat, Function.identity()));
		Importer importer = map.get(format);
		if (importer == null) {
			throw new RuntimeException("Unsupported import format: " + format);
		}
		return importer;
	}
}