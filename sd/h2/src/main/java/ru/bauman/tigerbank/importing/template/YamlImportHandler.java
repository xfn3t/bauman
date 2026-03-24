package ru.bauman.tigerbank.importing.template;

import org.springframework.stereotype.Component;
import ru.bauman.tigerbank.account.service.BankAccountService;
import ru.bauman.tigerbank.category.service.CategoryService;
import ru.bauman.tigerbank.export.ExportFormat;
import ru.bauman.tigerbank.importing.dto.ImportData;
import ru.bauman.tigerbank.importing.impl.YamlImporter;
import ru.bauman.tigerbank.operation.service.OperationService;
import java.io.InputStream;

@Component
public class YamlImportHandler extends AbstractImportHandler {

    private final YamlImporter yamlImporter;

    public YamlImportHandler(BankAccountService accountService,
                             CategoryService categoryService,
                             OperationService operationService,
                             YamlImporter yamlImporter) {
        super(accountService, categoryService, operationService);
        this.yamlImporter = yamlImporter;
    }

    @Override
    protected ImportData parseData(InputStream input) throws Exception {
        return yamlImporter.importData(input);
    }

    @Override
    public ExportFormat getFormat() {
        return ExportFormat.YAML;
    }
}