package ru.bauman.tigerbank.export.impl;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.export.ExportFormat;
import ru.bauman.tigerbank.export.Exporter;
import ru.bauman.tigerbank.operation.dto.OperationDto;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@Component
public class PdfExporter implements Exporter {

	@Override
	public void export(List<BankAccountDto> accounts, List<CategoryDto> categories, List<OperationDto> operations, OutputStream output) throws Exception {
		PdfDocument pdf = new PdfDocument(new PdfWriter(output));
		Document document = new Document(pdf);

		String PATH_TO_MAIN_FONT = "fonts/DejaVuSans.ttf";
		try (InputStream fontStream = getClass().getClassLoader().getResourceAsStream(PATH_TO_MAIN_FONT)) {
			if (fontStream != null) {
				byte[] fontBytes = fontStream.readAllBytes();
				PdfFont font = PdfFontFactory.createFont(fontBytes, "Identity-H");
				document.setFont(font);
			} else {
				System.err.println("Warning: DejaVuSans font not found, using default font.");
			}
		} catch (IOException e) {
			System.err.println("Failed to load DejaVuSans font: " + e.getMessage());
		}

		document.add(new Paragraph("Bank Accounts").setBold().setFontSize(14));
		Table accountTable = new Table(UnitValue.createPercentArray(new float[]{1, 3, 2}));
		accountTable.addHeaderCell("ID");
		accountTable.addHeaderCell("Name");
		accountTable.addHeaderCell("Balance");
		for (BankAccountDto acc : accounts) {
			accountTable.addCell(String.valueOf(acc.id()));
			accountTable.addCell(acc.name());
			accountTable.addCell(acc.balance().toString());
		}
		document.add(accountTable);

		document.add(new Paragraph("\nCategories").setBold().setFontSize(14));
		Table catTable = new Table(UnitValue.createPercentArray(new float[]{1, 3, 1, 2}));
		catTable.addHeaderCell("ID");
		catTable.addHeaderCell("Name");
		catTable.addHeaderCell("TypeID");
		catTable.addHeaderCell("TypeName");
		for (CategoryDto cat : categories) {
			catTable.addCell(String.valueOf(cat.id()));
			catTable.addCell(cat.name());
			catTable.addCell(String.valueOf(cat.type().id()));
			catTable.addCell(cat.type().name());
		}
		document.add(catTable);

		document.add(new Paragraph("\nOperations").setBold().setFontSize(14));
		Table opTable = new Table(UnitValue.createPercentArray(new float[]{1, 2, 3, 2, 1, 1, 1}));
		opTable.addHeaderCell("ID");
		opTable.addHeaderCell("Amount");
		opTable.addHeaderCell("Date");
		opTable.addHeaderCell("Description");
		opTable.addHeaderCell("TypeID");
		opTable.addHeaderCell("AccountID");
		opTable.addHeaderCell("CategoryID");
		for (OperationDto op : operations) {
			opTable.addCell(String.valueOf(op.id()));
			opTable.addCell(op.amount().toString());
			opTable.addCell(op.date().toString());
			opTable.addCell(op.description());
			opTable.addCell(String.valueOf(op.type().id()));
			opTable.addCell(String.valueOf(op.account().id()));
			opTable.addCell(String.valueOf(op.category().id()));
		}
		document.add(opTable);

		document.close();
	}

	@Override
	public ExportFormat getFormat() {
		return ExportFormat.PDF;
	}
}