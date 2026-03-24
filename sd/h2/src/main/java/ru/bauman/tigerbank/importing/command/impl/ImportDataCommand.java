package ru.bauman.tigerbank.importing.command.impl;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.bauman.tigerbank.common.command.Command;
import ru.bauman.tigerbank.export.ExportFormat;
import ru.bauman.tigerbank.importing.template.AbstractImportHandler;

@RequiredArgsConstructor
public class ImportDataCommand implements Command<Void> {

    private final Map<ExportFormat, AbstractImportHandler> handlers;
    private final ExportFormat format;
    private final String filePath;

    @Override
    @SneakyThrows
    public Void execute() {
        AbstractImportHandler handler = handlers.get(format);
        if (handler == null) {
            throw new RuntimeException("No import handler for format: " + format);
        }
        handler.handle(filePath);
        return null;
    }
}