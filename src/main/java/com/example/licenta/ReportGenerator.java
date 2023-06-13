package com.example.licenta;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReportGenerator {
    public static void main(String[] args) throws IOException, DocumentException, URISyntaxException {

        // Inserting text
//        Document document = new Document();
//        PdfWriter.getInstance(document, new FileOutputStream("/home/stanciul420/Desktop/Report.pdf"));
//
//        document.open();
//        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
//        Chunk chunk = new Chunk("Hello World", font);
//
//        document.add(chunk);
//        document.close();


        // Inserting images
//        Path path = Paths.get(ClassLoader.getSystemResource("Java_logo.png").toURI());
//
//        Document document = new Document();
//        PdfWriter.getInstance(document, new FileOutputStream("/home/stanciul420/Desktop/Report.pdf"));
//        document.open();
//        Image img = Image.getInstance(path.toAbsolutePath().toString());
//        document.add(img);
//
//        document.close();

        //Inserting tables
//        Document document = new Document();
//        PdfWriter.getInstance(document, new FileOutputStream("iTextTable.pdf"));
//
//        document.open();
//
//        PdfPTable table = new PdfPTable(3);
//        addTableHeader(table);
//        addRows(table);
//        addCustomRows(table);
//
//        document.add(table);
//        document.close();
    }
}
