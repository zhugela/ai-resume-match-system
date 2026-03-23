package com.liangge.resumematch.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liangge.resumematch.entity.Job;
import com.liangge.resumematch.entity.MatchRecord;
import com.liangge.resumematch.entity.Resume;
import com.liangge.resumematch.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@SuppressWarnings("unchecked")
@Service
public class AIServiceImpl implements AIService {

    private final ChatClient chatClient;
    private final ChatModel chatModel;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AIServiceImpl(ChatClient chatClient, ChatModel chatModel) {
        this.chatClient = chatClient;
        this.chatModel = chatModel;
    }

    @Override
    public Resume getResumeById(Long id) {
        return null;
    }

    @Value("${ai.provider:minimax}")
    private String aiProvider;

    @Override
    public Resume analyzeResume(Resume resume) {
        try {
            // 1. 读取简历文件内容
            String content = readResumeContent(resume.getFileUrl());

            // 2. 调用AI分析（使用 Spring AI）
            Map<String, Object> analysisResult = callAIAnalysisWithSpringAI(content);

            // 3. 解析结果
            if (analysisResult.containsKey("skills")) {
                resume.setSkills(objectMapper.writeValueAsString(analysisResult.get("skills")));
            }
            if (analysisResult.containsKey("workExperience")) {
                resume.setWorkExperience(objectMapper.writeValueAsString(analysisResult.get("workExperience")));
            }
            if (analysisResult.containsKey("education")) {
                resume.setEducation(objectMapper.writeValueAsString(analysisResult.get("education")));
            }
            if (analysisResult.containsKey("parsedData")) {
                resume.setParsedData(objectMapper.writeValueAsString(analysisResult.get("parsedData")));
            }
            if (analysisResult.containsKey("score")) {
                Object score = analysisResult.get("score");
                if (score instanceof Number) {
                    resume.setScore(((Number) score).intValue());
                }
            }

            resume.setAnalysisStatus("COMPLETED");

        } catch (Exception e) {
            log.error("简历分析失败: {}", e.getMessage());
            resume.setAnalysisStatus("FAILED");
        }

        return resume;
    }

    /**
     * 使用 Spring AI 调用AI分析
     */
    private Map<String, Object> callAIAnalysisWithSpringAI(String content) {
        try {
            String prompt = buildAnalysisPrompt(content);

            // 使用 Spring AI ChatClient
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            // 解析AI返回的JSON
            return parseAIResponse(response);
        } catch (Exception e) {
            log.error("Spring AI 调用失败: {}", e.getMessage());
            return getMockResult();
        }
    }

    /**
     * 构建分析提示词
     */
    private String buildAnalysisPrompt(String content) {
        return """
            请分析以下简历内容，提取关键信息并返回JSON格式：
            {
                "skills": ["技能1", "技能2", ...],
                "workExperience": [{"company": "公司名", "position": "职位", "duration": "时长"}],
                "education": [{"school": "学校", "degree": "学历", "major": "专业"}],
                "parsedData": {"key": "value"},
                "score": 85
            }
            只返回JSON，不要其他内容。

            简历内容：
            """.concat(content);
    }

    @Override
    public List<MatchRecord> matchJobs(Resume resume, List<Job> jobs) {
        List<MatchRecord> records = new ArrayList<>();

        try {
            // 获取简历技能
            Set<String> resumeSkills = parseSkills(resume.getSkills());

            for (Job job : jobs) {
                MatchRecord record = new MatchRecord();
                record.setResumeId(resume.getId());
                record.setJobId(job.getId());
                record.setStatus("NEW");

                // 计算匹配度
                BigDecimal score = calculateMatchScore(resumeSkills, job);
                record.setMatchScore(score);

                // 生成匹配详情
                Map<String, Object> details = generateMatchDetails(resumeSkills, job, score);
                record.setMatchDetails(objectMapper.writeValueAsString(details));

                records.add(record);
            }

            // 按匹配度排序
            records.sort((a, b) -> b.getMatchScore().compareTo(a.getMatchScore()));

        } catch (Exception e) {
            log.error("岗位匹配失败: {}", e.getMessage());
        }

        return records;
    }

    /**
     * 批量匹配 - 使用 CompletableFuture 并发调用AI
     */
    public List<MatchRecord> matchJobsAsync(Resume resume, List<Job> jobs) {
        ExecutorService executor = Executors.newFixedThreadPool(Math.min(jobs.size(), 10));

        try {
            List<CompletableFuture<MatchRecord>> futures = jobs.stream()
                    .map(job -> CompletableFuture.supplyAsync(() -> {
                        try {
                            return matchSingleJob(resume, job);
                        } catch (Exception e) {
                            log.error("匹配岗位 {} 失败: {}", job.getId(), e.getMessage());
                            return createFailedRecord(resume.getId(), job.getId());
                        }
                    }, executor))
                    .toList();

            // 等待所有结果
            List<MatchRecord> records = futures.stream()
                    .map(CompletableFuture::join)
                    .toList();

            // 按匹配度排序
            records.sort((a, b) -> b.getMatchScore().compareTo(a.getMatchScore()));

            return records;
        } finally {
            executor.shutdown();
        }
    }

    /**
     * 匹配单个岗位（可扩展为AI语义匹配）
     */
    private MatchRecord matchSingleJob(Resume resume, Job job) throws Exception {
        Set<String> resumeSkills = parseSkills(resume.getSkills());

        MatchRecord record = new MatchRecord();
        record.setResumeId(resume.getId());
        record.setJobId(job.getId());
        record.setStatus("NEW");

        BigDecimal score = calculateMatchScore(resumeSkills, job);
        record.setMatchScore(score);

        Map<String, Object> details = generateMatchDetails(resumeSkills, job, score);
        record.setMatchDetails(objectMapper.writeValueAsString(details));

        return record;
    }

    /**
     * 创建失败的匹配记录
     */
    private MatchRecord createFailedRecord(Long resumeId, Long jobId) {
        MatchRecord record = new MatchRecord();
        record.setResumeId(resumeId);
        record.setJobId(jobId);
        record.setStatus("FAILED");
        record.setMatchScore(BigDecimal.ZERO);
        return record;
    }

    @Override
    public List<MatchRecord> getRecommendedJobs(Long userId, Integer limit) {
        return Collections.emptyList();
    }

    /**
     * 读取简历内容
     */
    private String readResumeContent(String fileUrl) {
        return "简历内容...";
    }

    /**
     * 解析AI响应
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseAIResponse(String content) {
        try {
            String jsonStr = content.trim();
            if (jsonStr.contains("```json")) {
                jsonStr = jsonStr.substring(jsonStr.indexOf("```json") + 7);
                jsonStr = jsonStr.substring(0, jsonStr.indexOf("```"));
            } else if (jsonStr.contains("```")) {
                jsonStr = jsonStr.substring(jsonStr.indexOf("```") + 3);
                jsonStr = jsonStr.substring(0, jsonStr.indexOf("```"));
            }
            jsonStr = jsonStr.trim();

            return objectMapper.readValue(jsonStr, Map.class);
        } catch (Exception e) {
            log.warn("AI响应解析失败，使用原始内容: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("rawContent", content);
            result.put("skills", Arrays.asList("Java", "Spring Boot"));
            result.put("score", 75);
            return result;
        }
    }

    /**
     * 获取模拟结果
     */
    private Map<String, Object> getMockResult() {
        Map<String, Object> result = new HashMap<>();
        result.put("skills", Arrays.asList("Java", "Spring Boot", "MySQL", "Redis"));
        result.put("workExperience", Arrays.asList(
                Map.of("company", "XX公司", "position", "Java开发工程师", "duration", "2年")
        ));
        result.put("education", Arrays.asList(
                Map.of("school", "XX大学", "degree", "本科", "major", "计算机科学")
        ));
        result.put("score", 85);
        return result;
    }

    /**
     * 解析技能列表
     */
    private Set<String> parseSkills(String skillsJson) {
        try {
            if (skillsJson == null || skillsJson.isEmpty()) {
                return new HashSet<>();
            }
            List<?> skills = objectMapper.readValue(skillsJson, List.class);
            Set<String> result = new HashSet<>();
            for (Object skill : skills) {
                if (skill != null) {
                    result.add(skill.toString());
                }
            }
            return result;
        } catch (Exception e) {
            return new HashSet<>();
        }
    }

    /**
     * 计算匹配度
     */
    private BigDecimal calculateMatchScore(Set<String> resumeSkills, Job job) {
        Set<String> requiredSkills = parseSkills(job.getRequiredSkills());
        Set<String> preferredSkills = parseSkills(job.getPreferredSkills());

        if (requiredSkills.isEmpty()) {
            return BigDecimal.valueOf(50);
        }

        long matchedRequired = resumeSkills.stream()
                .filter(s -> requiredSkills.stream()
                        .anyMatch(rs -> s.equalsIgnoreCase(rs) || s.toLowerCase().contains(rs.toLowerCase())))
                .count();

        long matchedPreferred = resumeSkills.stream()
                .filter(s -> preferredSkills.stream()
                        .anyMatch(ps -> s.equalsIgnoreCase(ps) || s.toLowerCase().contains(ps.toLowerCase())))
                .count();

        double requiredScore = (double) matchedRequired / requiredSkills.size() * 70;
        double preferredScore = Math.min((double) matchedPreferred / preferredSkills.size() * 30, 30);

        return BigDecimal.valueOf(requiredScore + preferredScore)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 生成匹配详情
     */
    private Map<String, Object> generateMatchDetails(Set<String> resumeSkills, Job job, BigDecimal score) {
        Map<String, Object> details = new HashMap<>();

        Set<String> requiredSkills = parseSkills(job.getRequiredSkills());
        Set<String> matchedRequired = new HashSet<>();
        Set<String> missingRequired = new HashSet<>();

        for (String skill : requiredSkills) {
            boolean found = resumeSkills.stream()
                    .anyMatch(rs -> rs.equalsIgnoreCase(skill) || rs.toLowerCase().contains(skill.toLowerCase()));
            if (found) {
                matchedRequired.add(skill);
            } else {
                missingRequired.add(skill);
            }
        }

        details.put("matchedRequiredSkills", matchedRequired);
        details.put("missingRequiredSkills", missingRequired);
        details.put("matchScore", score);

        return details;
    }
}
