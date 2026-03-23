package com.liangge.resumematch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liangge.resumematch.entity.ExportRecord;
import com.liangge.resumematch.entity.Resume;
import com.liangge.resumematch.entity.ResumeVersion;
import com.liangge.resumematch.mapper.ExportRecordMapper;
import com.liangge.resumematch.service.ExportService;
import com.liangge.resumematch.service.ResumeService;
import com.liangge.resumematch.service.ResumeVersionService;
import com.liangge.resumematch.utils.DocxExporter;
import com.liangge.resumematch.utils.PdfExporter;
import com.liangge.resumematch.utils.ResumeParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportServiceImpl extends ServiceImpl<ExportRecordMapper, ExportRecord> implements ExportService {

    private final ResumeService resumeService;
    private final ResumeVersionService resumeVersionService;
    private final ResumeParser resumeParser;
    private final DocxExporter docxExporter;
    private final PdfExporter pdfExporter;

    @Value("${file.export-dir:./exports}")
    private String exportDir;

    @Override
    public ExportRecord exportComparison(Long resumeId, Long versionId, String exportType) {
        // 获取简历
        Resume resume = resumeService.getById(resumeId);
        if (resume == null) {
            throw new RuntimeException("简历不存在");
        }

        // 获取版本信息
        ResumeVersion version = null;
        String originalContent = "";
        String optimizedContent = "";
        String optimizationSuggestions = "";
        String interviewQuestions = "";

        if (versionId != null) {
            version = resumeVersionService.getVersionById(versionId);
        } else {
            version = resumeVersionService.getLatestVersion(resumeId);
        }

        if (version != null) {
            originalContent = version.getOriginalContent();
            optimizedContent = version.getOptimizedContent();
            optimizationSuggestions = version.getOptimizationSuggestions();
            interviewQuestions = version.getInterviewQuestions();
        }

        // 解析简历获取姓名
        String name = "";
        try {
            String fileContent = resumeParser.parseToText(resume.getFileUrl());
            var parseResult = resumeParser.parseToStructure(fileContent);
            name = parseResult.getName();
        } catch (IOException e) {
            // 忽略解析错误
        }

        // 解析面试题（JSON 字符串转列表）
        List<String> questionsList = parseQuestions(interviewQuestions);

        try {
            String fileUrl;
            String fileName;

            if ("PDF".equalsIgnoreCase(exportType)) {
                fileUrl = pdfExporter.exportComparison(
                    name, originalContent, optimizedContent,
                    optimizationSuggestions, questionsList, exportDir);
                fileName = "resume_comparison.pdf";
            } else {
                // 默认导出 Word
                fileUrl = docxExporter.exportComparison(
                    name, originalContent, optimizedContent,
                    optimizationSuggestions, questionsList, exportDir);
                fileName = "resume_comparison.docx";
            }

            // 保存导出记录
            ExportRecord record = new ExportRecord();
            record.setResumeId(resumeId);
            record.setVersionId(version != null ? version.getId() : null);
            record.setExportType("COMPARISON");
            record.setFileUrl(fileUrl);
            record.setFileName(fileName);
            save(record);

            return record;

        } catch (IOException e) {
            throw new RuntimeException("导出失败: " + e.getMessage());
        }
    }

    @Override
    public List<ExportRecord> getExportsByResumeId(Long resumeId) {
        return list(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExportRecord>()
                .eq(ExportRecord::getResumeId, resumeId)
                .orderByDesc(ExportRecord::getCreatedAt));
    }

    private List<String> parseQuestions(String questions) {
        if (questions == null || questions.isEmpty()) {
            return List.of();
        }
        // 尝试解析为列表
        try {
            if (questions.startsWith("[")) {
                return Arrays.stream(questions.replaceAll("[\\[\\]\"]", "").split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            // 忽略
        }
        // 逐行解析
        return Arrays.stream(questions.split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
