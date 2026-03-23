package com.liangge.resumematch.utils;

import com.liangge.resumematch.dto.ResumeParseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class ResumeParser {

    private final PdfParser pdfParser;
    private final DocxParser docxParser;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final Pattern PHONE_PATTERN = Pattern.compile("1[3-9]\\d{9}");

    /**
     * 解析简历文件，自动识别类型
     */
    public String parseToText(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        
        String lowerName = fileName.toLowerCase();
        
        if (lowerName.endsWith(".pdf")) {
            return pdfParser.parse(file);
        } else if (lowerName.endsWith(".docx")) {
            return docxParser.parse(file);
        } else if (lowerName.endsWith(".doc")) {
            // DOC 也尝试用 DOCX 解析
            return docxParser.parse(file);
        } else if (lowerName.endsWith(".txt")) {
            return new String(file.getBytes());
        } else {
            throw new IllegalArgumentException("不支持的文件类型: " + fileName);
        }
    }

    /**
     * 解析简历文件（从文件路径）
     */
    public String parseToText(String filePath) throws IOException {
        String lowerPath = filePath.toLowerCase();
        
        if (lowerPath.endsWith(".pdf")) {
            return pdfParser.parse(filePath);
        } else if (lowerPath.endsWith(".docx") || lowerPath.endsWith(".doc")) {
            return docxParser.parse(filePath);
        } else if (lowerPath.endsWith(".txt")) {
            return java.nio.file.Files.readString(java.nio.file.Paths.get(filePath));
        } else {
            throw new IllegalArgumentException("不支持的文件类型: " + filePath);
        }
    }

    /**
     * 从文本内容提取结构化信息
     */
    public ResumeParseResult parseToStructure(String text) {
        ResumeParseResult result = new ResumeParseResult();
        
        // 提取邮箱
        Matcher emailMatcher = EMAIL_PATTERN.matcher(text);
        if (emailMatcher.find()) {
            result.setEmail(emailMatcher.group());
        }
        
        // 提取手机号
        Matcher phoneMatcher = PHONE_PATTERN.matcher(text);
        if (phoneMatcher.find()) {
            result.setPhone(phoneMatcher.group());
        }
        
        // 简单提取姓名（假设是第一行或包含"姓名"的行）
        String[] lines = text.split("\\n");
        if (lines.length > 0) {
            String firstLine = lines[0].trim();
            if (!firstLine.isEmpty() && firstLine.length() < 20) {
                result.setName(firstLine);
            }
        }
        
        // 提取技能关键词
        result.setSkills(extractSkills(text));
        
        // 设置原始文本
        result.setRawData(java.util.Map.of("rawText", text));
        
        return result;
    }

    /**
     * 提取技能关键词
     */
    private java.util.List<String> extractSkills(String text) {
        java.util.List<String> skills = new java.util.ArrayList<>();
        
        String[] skillKeywords = {
            "Java", "Python", "Go", "C++", "C#", "JavaScript", "TypeScript", "PHP", "Ruby", "Swift",
            "Spring", "Spring Boot", "Spring Cloud", "Django", "Flask", "Express", "React", "Vue", "Angular",
            "MySQL", "PostgreSQL", "MongoDB", "Redis", "Elasticsearch", "Kafka", "RabbitMQ",
            "Docker", "Kubernetes", "Jenkins", "Git", "Linux", "AWS", "Azure", "GCP",
            "TensorFlow", "PyTorch", "Machine Learning", "Deep Learning", "NLP",
            "HTML", "CSS", "REST", "API", "Microservice", "OOP", "Design Patterns"
        };
        
        String lowerText = text.toLowerCase();
        for (String skill : skillKeywords) {
            if (lowerText.contains(skill.toLowerCase())) {
                skills.add(skill);
            }
        }
        
        return skills;
    }
}
