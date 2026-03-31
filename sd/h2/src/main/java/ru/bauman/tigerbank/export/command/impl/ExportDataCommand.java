package ru.bauman.tigerbank.export.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.bauman.tigerbank.common.command.Command;
import ru.bauman.tigerbank.export.ExportFormat;
import ru.bauman.tigerbank.export.ExportService;

@RequiredArgsConstructor
public class ExportDataCommand implements Command<Void> {

    private final ExportService exportService;
    private final ExportFormat format;
    private final String filePath;

    @Override
    @SneakyThrows
    public Void execute() {
        exportService.exportData(format, filePath);
        return null;
    }
}