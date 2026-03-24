package ru.bauman.tigerbank.importing.template;

import org.springframework.stereotype.Component;
import ru.bauman.tigerbank.account.service.BankAccountService;
import ru.bauman.tigerbank.category.service.CategoryService;
import ru.bauman.tigerbank.export.ExportFormat;
import ru.bauman.tigerbank.importing.dto.ImportData;
import ru.bauman.tigerbank.importing.impl.CsvImporter;
import ru.bauman.tigerbank.operation.service.OperationService;
import java.io.InputStream;

@Component
public class CsvImportHandler extends AbstractImportHandler {

    private final CsvImporter csvImporter;

    public CsvImportHandler(BankAccountService accountService,
                            CategoryService categoryService,
                            OperationService operationService,
                            CsvImporter csvImporter) {
        super(accountService, categoryService, operationService);
        this.csvImporter = csvImporter;
    }

    @Override
    protected ImportData parseData(InputStream input) throws Exception {
        return csvImporter.importData(input);
    }

    @Override
    public ExportFormat getFormat() {
        return ExportFormat.CSV;
    }
}