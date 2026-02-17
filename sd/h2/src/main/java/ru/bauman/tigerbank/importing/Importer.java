package ru.bauman.tigerbank.importing;

import ru.bauman.tigerbank.export.ExportFormat;
import java.io.InputStream;

public interface Importer {
	ImportData importData(InputStream input) throws Exception;
	ExportFormat getFormat();
}