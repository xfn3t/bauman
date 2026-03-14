package ru.bauman.tigerbank.export.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.export.ExportFormat;
import ru.bauman.tigerbank.export.Exporter;
import ru.bauman.tigerbank.operation.dto.OperationDto;
import org.springframework.stereotype.Component;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class YamlExporter implements Exporter {
	private final ObjectMapper objectMapper;

	public YamlExporter() {
		this.objectMapper = new ObjectMapper(new YAMLFactory());
		this.objectMapper.registerModule(new JavaTimeModule());
	}

	@Override
	public void export(List<BankAccountDto> accounts, List<CategoryDto> categories, List<OperationDto> operations, OutputStream output) throws Exception {
		Map<String, Object> data = new HashMap<>();
		data.put("accounts", accounts);
		data.put("categories", categories);
		data.put("operations", operations);
		objectMapper.writeValue(output, data);
	}

	@Override
	public ExportFormat getFormat() {
		return ExportFormat.YAML;
	}
}