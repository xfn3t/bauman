package ru.bauman.tigerbank.importing.template;

import org.springframework.stereotype.Component;
import ru.bauman.tigerbank.account.service.BankAccountService;
import ru.bauman.tigerbank.category.service.CategoryService;
import ru.bauman.tigerbank.export.ExportFormat;
import ru.bauman.tigerbank.importing.dto.ImportData;
import ru.bauman.tigerbank.importing.impl.JsonImporter;
import ru.bauman.tigerbank.operation.service.OperationService;
import java.io.InputStream;

@Component
public class JsonImportHandler extends AbstractImportHandler {

    private final JsonImporter jsonImporter;

    public JsonImportHandler(BankAccountService accountService,
                             CategoryService categoryService,
                             OperationService operationService,
                             JsonImporter jsonImporter) {
        super(accountService, categoryService, operationService);
        this.jsonImporter = jsonImporter;
    }

    @Override
    protected ImportData parseData(InputStream input) throws Exception {
        return jsonImporter.importData(input);
    }

    @Override
    public ExportFormat getFormat() {
        return ExportFormat.JSON;
    }
}