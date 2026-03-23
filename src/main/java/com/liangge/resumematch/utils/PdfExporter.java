package com.liangge.resumematch.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PdfExporter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * 导出优化对比报告（PDF格式）
     */
    public String exportComparison(
            String name,
            String originalContent,
            String optimizedContent,
            String optimizationSuggestions,
            List<String> interviewQuestions,
            String outputDir) throws IOException {
        
        // 创建输出目录
        Path dirPath = Path.of(outputDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // 生成文件名
        String fileName = "resume_comparison_" + LocalDateTime.now().format(FORMATTER) + ".pdf";
        Path filePath = dirPath.resolve(fileName);

        // 使用 PDFBox 创建 PDF
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float yPosition = 750;
                float margin = 50;
                float fontSize = 11;
                float titleFontSize = 18;
                float headingFontSize = 14;
                float lineHeight = 15;

                PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                // 标题
                contentStream.beginText();
                contentStream.setFont(fontBold, titleFontSize);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("简历优化对比报告");
                contentStream.endText();
                yPosition -= 40;

                // 基本信息
                contentStream.beginText();
                contentStream.setFont(fontBold, headingFontSize);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("基本信息");
                contentStream.endText();
                yPosition -= 20;

                contentStream.beginText();
                contentStream.setFont(fontRegular, fontSize);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("姓名: " + (name != null ? name : "未识别"));
                contentStream.endText();
                yPosition -= lineHeight;

                contentStream.beginText();
                contentStream.setFont(fontRegular, fontSize);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("生成时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                contentStream.endText();
                yPosition -= 30;

                // 优化前内容
                yPosition = drawHeading(contentStream, "优化前简历内容", margin, yPosition, fontBold, headingFontSize);
                yPosition = drawContent(contentStream, originalContent, margin, yPosition, fontRegular, fontSize, lineHeight, 540);

                // 优化后内容
                yPosition = drawHeading(contentStream, "优化后简历内容", margin, yPosition, fontBold, headingFontSize);
                yPosition = drawContent(contentStream, optimizedContent, margin, yPosition, fontRegular, fontSize, lineHeight, 540);

                // 优化建议
                yPosition = drawHeading(contentStream, "优化建议", margin, yPosition, fontBold, headingFontSize);
                yPosition = drawContent(contentStream, optimizationSuggestions, margin, yPosition, fontRegular, fontSize, lineHeight, 540);

                // 面试题
                if (interviewQuestions != null && !interviewQuestions.isEmpty()) {
                    yPosition = drawHeading(contentStream, "针对性面试题", margin, yPosition, fontBold, headingFontSize);
                    int i = 1;
                    for (String question : interviewQuestions) {
                        yPosition = drawContent(contentStream, i + ". " + question, margin, yPosition, fontRegular, fontSize, lineHeight, 540);
                        i++;
                    }
                }
            }

            document.save(filePath.toFile());
        }

        return filePath.toString();
    }

    private float drawHeading(PDPageContentStream contentStream, String text, float x, float y, PDType1Font font, float fontSize) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
        return y - 20;
    }

    private float drawContent(PDPageContentStream contentStream, String text, float x, float y, PDType1Font font, float fontSize, float lineHeight, float maxWidth) throws IOException {
        if (text == null || text.isEmpty()) {
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(x, y);
            contentStream.showText("无");
            contentStream.endText();
            return y - lineHeight;
        }

        String[] lines = text.split("\n");
        for (String line : lines) {
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(x, y);
            contentStream.showText(line);
            contentStream.endText();
            y -= lineHeight;
        }
        return y - 10;
    }
}
