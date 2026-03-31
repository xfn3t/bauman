package ru.bauman.tigerbank.importing;

import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.account.service.BankAccountService;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.category.service.CategoryService;
import ru.bauman.tigerbank.export.ExportFormat;
import ru.bauman.tigerbank.importing.dto.ImportData;
import ru.bauman.tigerbank.operation.dto.OperationDto;
import ru.bauman.tigerbank.operation.service.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportService {

	private final List<Importer> importers;
	private final BankAccountService accountService;
	private final CategoryService categoryService;
	private final OperationService operationService;

	@Transactional
	public void importData(ExportFormat format, String filePath) throws Exception {
		Importer importer = findImporter(format);
		try (InputStream is = new FileInputStream(filePath)) {
			ImportData data = importer.importData(is);

			// Маппинг старых id на новые (или те же) для счетов
			Map<Long, Long> accountIdMapping = new HashMap<>();
			for (BankAccountDto dto : data.accounts()) {
				BankAccountDto saved;
				try {
					// Пытаемся найти по id из файла
					saved = accountService.findById(dto.id());
					// Если найден, обновляем поля (кроме id)
					BankAccountDto updateDto = new BankAccountDto(saved.id(), dto.name(), dto.balance());
					saved = accountService.update(saved.id(), updateDto);
				} catch (RuntimeException e) {
					// создаем новый если не найден
					BankAccountDto newDto = new BankAccountDto(null, dto.name(), dto.balance());
					saved = accountService.create(newDto);
				}
				accountIdMapping.put(dto.id(), saved.id());
			}

			// Маппинг старых id на новые (или те же) для категорий
			Map<Long, Long> categoryIdMapping = new HashMap<>();
			for (CategoryDto dto : data.categories()) {
				CategoryDto saved;
				try {
					saved = categoryService.findById(dto.id());
					// Обновляем категорию (имя и тип)
					CategoryDto updateDto = new CategoryDto(saved.id(), dto.name(), dto.type());
					saved = categoryService.update(saved.id(), updateDto);
				} catch (RuntimeException e) {
					CategoryDto newDto = new CategoryDto(null, dto.name(), dto.type());
					saved = categoryService.create(newDto);
				}
				categoryIdMapping.put(dto.id(), saved.id());
			}

			// Импорт/обновление операций
			for (OperationDto dto : data.operations()) {
				// Подменяем id счета и категории на актуальные
				Long actualAccountId = accountIdMapping.get(dto.account().id());
				Long actualCategoryId = categoryIdMapping.get(dto.category().id());
				if (actualAccountId == null || actualCategoryId == null) {
					throw new RuntimeException("Не найдено соответствие для счёта или категории при импорте операции: " + dto);
				}

				// DTO с актуальными ID
				BankAccountDto accountRef = new BankAccountDto(actualAccountId, null, null);
				CategoryDto categoryRef = new CategoryDto(actualCategoryId, null, null);
				OperationDto opDto = new OperationDto(
						dto.id(),
						dto.amount(),
						dto.date(),
						dto.description(),
						dto.type(),
						accountRef,
						categoryRef
				);

				try {
					// Пытаемся найти операцию по id из файла
					OperationDto existing = operationService.findById(dto.id());
					// Если найдена, обновляем
					operationService.update(existing.id(), opDto);
				} catch (RuntimeException e) {
					// создаем новую если не найдено
					OperationDto newOpDto = new OperationDto(
							null,
							dto.amount(),
							dto.date(),
							dto.description(),
							dto.type(),
							accountRef,
							categoryRef
					);
					operationService.create(newOpDto);
				}
			}
		}
	}

	private Importer findImporter(ExportFormat format) {
		Map<ExportFormat, Importer> map = importers.stream()
				.collect(Collectors.toMap(Importer::getFormat, Function.identity()));
		Importer importer = map.get(format);
		if (importer == null) {
			throw new RuntimeException("Неподдерживаемый формат импорта: " + format);
		}
		return importer;
	}
}