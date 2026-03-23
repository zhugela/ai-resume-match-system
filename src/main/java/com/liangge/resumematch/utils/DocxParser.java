package com.liangge.resumematch.utils;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class DocxParser {

    /**
     * 从 DOCX 文件提取文本内容
     */
    public String parse(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            return parse(inputStream);
        }
    }

    /**
     * 从 DOCX 文件路径提取文本内容
     */
    public String parse(String filePath) throws IOException {
        try (XWPFDocument document = new XWPFDocument(new java.io.FileInputStream(filePath))) {
            return extractText(document);
        }
    }

    /**
     * 从 InputStream 提取文本内容
     */
    public String parse(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            return extractText(document);
        }
    }

    private String extractText(XWPFDocument document) {
        StringBuilder sb = new StringBuilder();
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            String text = paragraph.getText();
            if (text != null && !text.trim().isEmpty()) {
                sb.append(text).append("\n");
            }
        }
        return sb.toString();
    }
}
