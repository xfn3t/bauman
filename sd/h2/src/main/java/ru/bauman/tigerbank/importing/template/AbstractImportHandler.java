package ru.bauman.tigerbank.importing.template;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.account.service.BankAccountService;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.category.service.CategoryService;
import ru.bauman.tigerbank.export.ExportFormat;
import ru.bauman.tigerbank.importing.dto.ImportData;
import ru.bauman.tigerbank.operation.dto.OperationDto;
import ru.bauman.tigerbank.operation.service.OperationService;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractImportHandler {

    private final BankAccountService accountService;
    private final CategoryService categoryService;
    private final OperationService operationService;

    @Transactional
    public final void handle(String filePath) throws Exception {
        log.info("Starting import from {} (format: {})", filePath, getFormat());
        try (InputStream is = new FileInputStream(filePath)) {
            ImportData data = parseData(is);
            validateData(data);
            ImportResult result = processData(data);
            postProcess(result);
        }
        log.info("Import completed for format: {}", getFormat());
    }

    protected abstract ImportData parseData(InputStream input) throws Exception;

    public abstract ExportFormat getFormat();

    protected void validateData(ImportData data) {
        if (data == null) {
            throw new IllegalArgumentException("ImportData cannot be null");
        }
    }

    protected ImportResult processData(ImportData data) {
        Map<Long, Long> accountIdMapping = new HashMap<>();
        for (BankAccountDto dto : data.accounts()) {
            BankAccountDto saved = upsertAccount(dto);
            accountIdMapping.put(dto.id(), saved.id());
        }

        Map<Long, Long> categoryIdMapping = new HashMap<>();
        for (CategoryDto dto : data.categories()) {
            CategoryDto saved = upsertCategory(dto);
            categoryIdMapping.put(dto.id(), saved.id());
        }

        for (OperationDto dto : data.operations()) {
            Long actualAccountId = accountIdMapping.get(dto.account().id());
            Long actualCategoryId = categoryIdMapping.get(dto.category().id());
            if (actualAccountId == null || actualCategoryId == null) {
                throw new RuntimeException("No mapping found for account/category during operation import: " + dto);
            }
            upsertOperation(dto, actualAccountId, actualCategoryId);
        }

        return new ImportResult(accountIdMapping.size(), categoryIdMapping.size(), data.operations().size());
    }

    protected void postProcess(ImportResult result) {
        log.info("Import result: {}", result);
    }

    private BankAccountDto upsertAccount(BankAccountDto dto) {
        try {
            BankAccountDto existing = accountService.findById(dto.id());
            return accountService.update(existing.id(), new BankAccountDto(existing.id(), dto.name(), dto.balance()));
        } catch (RuntimeException e) {
            return accountService.create(new BankAccountDto(null, dto.name(), dto.balance()));
        }
    }

    private CategoryDto upsertCategory(CategoryDto dto) {
        try {
            CategoryDto existing = categoryService.findById(dto.id());
            return categoryService.update(existing.id(), new CategoryDto(existing.id(), dto.name(), dto.type()));
        } catch (RuntimeException e) {
            return categoryService.create(new CategoryDto(null, dto.name(), dto.type()));
        }
    }

    private void upsertOperation(OperationDto dto, Long accountId, Long categoryId) {
        BankAccountDto accountRef = new BankAccountDto(accountId, null, null);
        CategoryDto categoryRef = new CategoryDto(categoryId, null, null);
        OperationDto mapped = new OperationDto(dto.id(), dto.amount(), dto.date(),
                dto.description(), dto.type(), accountRef, categoryRef);
        try {
            operationService.findById(dto.id());
            operationService.update(dto.id(), mapped);
        } catch (RuntimeException e) {
            operationService.create(new OperationDto(null, dto.amount(), dto.date(),
                    dto.description(), dto.type(), accountRef, categoryRef));
        }
    }

    public record ImportResult(int accounts, int categories, int operations) {}
}