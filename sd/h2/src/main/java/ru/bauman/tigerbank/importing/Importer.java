package ru.bauman.tigerbank.importing;

import ru.bauman.tigerbank.export.ExportFormat;
import ru.bauman.tigerbank.importing.dto.ImportData;

import java.io.InputStream;

public interface Importer {
	ImportData importData(InputStream input) throws Exception;
	ExportFormat getFormat();
}