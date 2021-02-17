package com.example.rafmak.invoice.entity;

import java.awt.Color;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import com.example.rafmak.billing.entity.BillingProducts;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoicePdfExporter {

	private Invoice invoice;
	
	private void writeTableHeader(PdfPTable table) {
		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(Color.blue);
		cell.setPadding(5);
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		font.setColor(Color.white);
		cell.setPhrase(new Phrase("Id",font));
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Product",font));
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Price",font));
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("qty",font));
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("total",font));
		table.addCell(cell);
	}
	
	private void writeTableData(PdfPTable table) {
		for (BillingProducts product : invoice.getProducts()) {
			table.addCell(product.getPid());
			table.addCell(product.getDescription());
			table.addCell(String.valueOf(product.getPrice()));
			table.addCell(String.valueOf(product.getQty()));
			table.addCell(String.valueOf(product.getItemTotal()));
			
		}
	}
	
	
	
	public void export(HttpServletResponse response) throws DocumentException, IOException {
		
		Document document = new Document(PageSize.A4);
		
		PdfWriter.getInstance(document, response.getOutputStream());
		
		document.open();
		
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		font.setColor(Color.black);
		font.setSize(18);
		Paragraph title = new Paragraph(invoice.getCompany().getCompanyName());
		document.add(title);
		
		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(100f);
		table.setWidths(new float[] {1.5f, 3.5f, 3.0f, 3.0f, 1.5f});
		table.setSpacingBefore(15);;
		writeTableHeader(table);
		writeTableData(table);
		document.add(table);
		
		Paragraph total = new Paragraph("TOTAL                         "+String.valueOf(invoice.getTotal()));
		document.add(total);
		
		
		document.close();
	}
}
