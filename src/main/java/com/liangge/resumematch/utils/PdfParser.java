package com.liangge.resumematch.utils;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component
public class PdfParser {

    /**
     * 从 PDF 文件提取文本内容
     */
    public String parse(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            return parse(inputStream);
        }
    }

    /**
     * 从 PDF 文件路径提取文本内容
     */
    public String parse(String filePath) throws IOException {
        try (PDDocument document = Loader.loadPDF(new java.io.File(filePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * 从 InputStream 提取文本内容
     */
    public String parse(InputStream inputStream) throws IOException {
        try (PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}
