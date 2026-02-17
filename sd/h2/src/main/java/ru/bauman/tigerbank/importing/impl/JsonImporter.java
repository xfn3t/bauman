package ru.bauman.tigerbank.importing.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.importing.ImportData;
import ru.bauman.tigerbank.importing.Importer;
import ru.bauman.tigerbank.operation.dto.OperationDto;
import ru.bauman.tigerbank.export.ExportFormat;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
public class JsonImporter implements Importer {
	private final ObjectMapper objectMapper;

	public JsonImporter() {
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new JavaTimeModule());
	}

	@Override
	public ImportData importData(InputStream input) throws Exception {
		Map<String, Object> map = objectMapper.readValue(input, Map.class);
		List<BankAccountDto> accounts = objectMapper.convertValue(map.get("accounts"), objectMapper.getTypeFactory().constructCollectionType(List.class, BankAccountDto.class));
		List<CategoryDto> categories = objectMapper.convertValue(map.get("categories"), objectMapper.getTypeFactory().constructCollectionType(List.class, CategoryDto.class));
		List<OperationDto> operations = objectMapper.convertValue(map.get("operations"), objectMapper.getTypeFactory().constructCollectionType(List.class, OperationDto.class));
		return new ImportData(accounts, categories, operations);
	}

	@Override
	public ExportFormat getFormat() {
		return ExportFormat.JSON;
	}
}