package com.liangge.resumematch.utils;

import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DocxExporter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * 导出优化对比报告（Word格式）
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
        String fileName = "resume_comparison_" + LocalDateTime.now().format(FORMATTER) + ".docx";
        Path filePath = dirPath.resolve(fileName);

        // 创建 Word 文档
        try (XWPFDocument document = new XWPFDocument()) {
            // 标题
            addTitle(document, "简历优化对比报告");

            // 基本信息
            addHeading(document, "基本信息");
            addParagraph(document, "姓名: " + (name != null ? name : "未识别"));
            addParagraph(document, "生成时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            // 优化前内容
            addHeading(document, "优化前简历内容");
            addParagraph(document, originalContent != null ? originalContent : "无");

            // 优化后内容
            addHeading(document, "优化后简历内容");
            addParagraph(document, optimizedContent != null ? optimizedContent : "无");

            // 优化建议
            addHeading(document, "优化建议");
            if (optimizationSuggestions != null && !optimizationSuggestions.isEmpty()) {
                addParagraph(document, optimizationSuggestions);
            } else {
                addParagraph(document, "暂无优化建议");
            }

            // 面试题
            if (interviewQuestions != null && !interviewQuestions.isEmpty()) {
                addHeading(document, "针对性面试题");
                int i = 1;
                for (String question : interviewQuestions) {
                    addParagraph(document, i + ". " + question);
                    i++;
                }
            }

            // 保存文件
            try (FileOutputStream out = new FileOutputStream(filePath.toFile())) {
                document.write(out);
            }
        }

        return filePath.toString();
    }

    /**
     * 添加标题
     */
    private void addTitle(XWPFDocument document, String title) {
        XWPFParagraph titlePara = document.createParagraph();
        titlePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText(title);
        titleRun.setBold(true);
        titleRun.setFontSize(20);
    }

    /**
     * 添加章节标题
     */
    private void addHeading(XWPFDocument document, String heading) {
        XWPFParagraph para = document.createParagraph();
        para.setSpacingAfter(200);
        XWPFRun run = para.createRun();
        run.setText(heading);
        run.setBold(true);
        run.setFontSize(14);
    }

    /**
     * 添加段落
     */
    private void addParagraph(XWPFDocument document, String text) {
        XWPFParagraph para = document.createParagraph();
        para.setSpacingAfter(100);
        XWPFRun run = para.createRun();
        run.setText(text);
        run.setFontSize(11);
    }
}
