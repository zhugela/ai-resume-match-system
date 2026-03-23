package com.liangge.resumematch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liangge.resumematch.dto.AnalysisResult;
import com.liangge.resumematch.entity.Resume;
import com.liangge.resumematch.mapper.ResumeMapper;
import com.liangge.resumematch.service.AiAnalysisService;
import com.liangge.resumematch.service.ResumeService;
import com.liangge.resumematch.utils.FileSecurityUtil;
import com.liangge.resumematch.utils.ResumeParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl extends ServiceImpl<ResumeMapper, Resume> implements ResumeService {

    private final ResumeParser resumeParser;
    private final AiAnalysisService aiAnalysisService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${file.upload-dir:./uploads/resumes}")
    private String uploadDir;

    @Override
    public Resume uploadResume(Long userId, String fileName, byte[] fileContent) {
        try {
            // 安全验证文件名
            String safeFileName = FileSecurityUtil.sanitizeFileName(fileName);
            
            // 验证文件扩展名
            String extension = FileSecurityUtil.getExtension(safeFileName);
            if (extension.isEmpty()) {
                throw new IllegalArgumentException("文件缺少扩展名");
            }

            // 创建上传目录（在用户隔离的子目录中）
            Path userUploadDir = Paths.get(uploadDir, "user_" + userId);
            if (!Files.exists(userUploadDir)) {
                Files.createDirectories(userUploadDir);
            }

            // 生成唯一文件名：UUID + 安全文件名
            String uniqueFileName = UUID.randomUUID().toString() + "_" + safeFileName;
            Path filePath = userUploadDir.resolve(uniqueFileName).normalize();

            // 防止路径穿越攻击
            if (!filePath.startsWith(userUploadDir.normalize())) {
                throw new IllegalArgumentException("无效的文件路径");
            }

            // 保存文件
            Files.write(filePath, fileContent);

            // 创建简历记录
            Resume resume = new Resume();
            resume.setUserId(userId);
            resume.setFileName(safeFileName);
            resume.setFileUrl(filePath.toString());
            resume.setFileType(getFileType(safeFileName));
            resume.setFileSize((long) fileContent.length);
            resume.setAnalysisStatus("PENDING");

            save(resume);
            return resume;

        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public void analyzeResume(Long resumeId) {
        Resume resume = getById(resumeId);
        if (resume == null) {
            throw new RuntimeException("简历不存在");
        }

        // 更新状态为处理中
        resume.setAnalysisStatus("PROCESSING");
        updateById(resume);

        try {
            // 1. 读取文件内容
            String content = resumeParser.parseToText(resume.getFileUrl());
            
            // 2. 调用 AI 分析
            AnalysisResult result = aiAnalysisService.analyzeResume(resume, content);
            
            // 3. 更新解析结果
            resume.setParsedData(objectMapper.writeValueAsString(result.getParsedData()));
            resume.setSkills(objectMapper.writeValueAsString(result.getSkills()));
            resume.setWorkExperience(result.getWorkExperience());
            resume.setEducation(result.getEducation());
            resume.setScore(result.getScore());
            resume.setAnalysisStatus("COMPLETED");
            
            updateById(resume);

        } catch (Exception e) {
            resume.setAnalysisStatus("FAILED");
            updateById(resume);
            throw new RuntimeException("简历分析失败: " + e.getMessage());
        }
    }

    /**
     * 优化简历（带目标岗位）
     */
    public AnalysisResult optimizeResume(Long resumeId, String jobDescription, Long jobId) {
        Resume resume = getById(resumeId);
        if (resume == null) {
            throw new RuntimeException("简历不存在");
        }

        try {
            String content = resumeParser.parseToText(resume.getFileUrl());
            return aiAnalysisService.optimizeResume(resume, content, 
                jobId != null ? jobId.toString() : null, jobDescription);
        } catch (IOException e) {
            throw new RuntimeException("简历解析失败: " + e.getMessage());
        }
    }

    private String getFileType(String fileName) {
        if (fileName == null) return "UNKNOWN";
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".pdf")) return "PDF";
        if (lower.endsWith(".docx")) return "DOCX";
        if (lower.endsWith(".doc")) return "DOC";
        if (lower.endsWith(".txt")) return "TXT";
        return "UNKNOWN";
    }
}
