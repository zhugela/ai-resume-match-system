package com.liangge.resumematch.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liangge.resumematch.dto.*;
import com.liangge.resumematch.entity.Job;
import com.liangge.resumematch.entity.MatchAnalysis;
import com.liangge.resumematch.entity.Resume;
import com.liangge.resumematch.mapper.MatchAnalysisMapper;
import com.liangge.resumematch.service.AiAnalysisService;
import com.liangge.resumematch.service.CacheService;
import com.liangge.resumematch.service.JobService;
import com.liangge.resumematch.service.ResumeVersionService;
import com.liangge.resumematch.utils.ResumeParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import com.fasterxml.jackson.core.type.TypeReference;

@Slf4j
@Service
public class AiAnalysisServiceImpl implements AiAnalysisService {

    private final ResumeParser resumeParser;
    private final JobService jobService;
    private final ResumeVersionService resumeVersionService;
    private final MatchAnalysisMapper matchAnalysisMapper;
    private final CacheService cacheService;
    private final com.liangge.resumematch.service.ResumeService resumeService;
    private final ObjectMapper objectMapper;

    @Value("${ai.minimax.api-key}")
    private String apiKey;

    @Value("${ai.minimax.base-url}")
    private String baseUrl;

    @Value("${ai.minimax.model}")
    private String model;

    // 构造函数注入
    public AiAnalysisServiceImpl(ResumeParser resumeParser, JobService jobService,
                                 ResumeVersionService resumeVersionService,
                                 MatchAnalysisMapper matchAnalysisMapper,
                                 CacheService cacheService,
                                 @Lazy com.liangge.resumematch.service.ResumeService resumeService) {
        this.resumeParser = resumeParser;
        this.jobService = jobService;
        this.resumeVersionService = resumeVersionService;
        this.matchAnalysisMapper = matchAnalysisMapper;
        this.cacheService = cacheService;
        this.resumeService = resumeService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public AnalysisResult analyzeResume(Resume resume, String resumeContent) {
        // 1. 尝试从缓存获取
        Object cached = cacheService.getResumeAnalysis(resume.getId());
        if (cached != null) {
            log.info("命中简历分析缓存: resumeId={}", resume.getId());
            return (AnalysisResult) cached;
        }

        AnalysisResult result = new AnalysisResult();
        
        try {
            // 2. 基础解析
            var parseResult = resumeParser.parseToStructure(resumeContent);
            result.setSkills(parseResult.getSkills());
            result.setParsedData(parseResult.getRawData());
            
            // 2. 调用 AI 进行深度分析
            String prompt = buildAnalysisPrompt(resumeContent);
            String aiResponse = callAi(prompt);
            
            // 3. 解析 AI 响应
            Map<String, Object> analysis = parseAiResponse(aiResponse);
            
            result.setScore((Integer) analysis.getOrDefault("score", 75));
            result.setWorkExperience((String) analysis.getOrDefault("workExperience", ""));
            result.setEducation((String) analysis.getOrDefault("education", ""));
            result.setOptimizationSuggestions((String) analysis.getOrDefault("optimizationSuggestions", ""));
            result.setInterviewQuestions(getStringList(analysis, "interviewQuestions"));
            
        } catch (Exception e) {
            log.error("简历分析失败", e);
            // 返回基础结果
            result.setScore(75);
            result.setSkills(new ArrayList<>());
            result.setOptimizationSuggestions("分析服务暂时不可用");
        }
        
        // 缓存结果
        cacheService.cacheResumeAnalysis(resume.getId(), result);
        
        return result;
    }

    @Override
    public AnalysisResult optimizeResume(Resume resume, String resumeContent, String targetJobId, String jobDescription) {
        AnalysisResult result = new AnalysisResult();
        
        try {
            // 1. 基础解析
            var parseResult = resumeParser.parseToStructure(resumeContent);
            result.setSkills(parseResult.getSkills());
            
            // 2. 调用 AI 进行简历优化
            String prompt = buildOptimizationPrompt(resumeContent, jobDescription);
            String aiResponse = callAi(prompt);
            
            // 3. 解析 AI 响应
            Map<String, Object> analysis = parseAiResponse(aiResponse);
            
            String optimizedContent = (String) analysis.getOrDefault("optimizedResume", resumeContent);
            String suggestions = (String) analysis.getOrDefault("optimizationSuggestions", "");
            List<String> questions = getStringList(analysis, "interviewQuestions");
            Integer score = (Integer) analysis.getOrDefault("matchScore", 80);
            
            result.setScore(score);
            result.setOptimizedContent(optimizedContent);
            result.setOptimizationSuggestions(suggestions);
            result.setInterviewQuestions(questions);
            
            // 4. 保存版本
            resumeVersionService.saveVersion(
                resume.getId(),
                resumeContent,
                optimizedContent,
                suggestions,
                String.join("\n", questions),
                score.doubleValue(),
                targetJobId != null ? Long.parseLong(targetJobId) : null
            );
            
        } catch (Exception e) {
            log.error("简历优化失败", e);
            result.setScore(75);
            result.setOptimizedContent(resumeContent);
            result.setOptimizationSuggestions("优化服务暂时不可用");
        }
        
        return result;
    }

    @Override
    public MatchResult matchWithJob(Resume resume, String resumeContent, MatchRequest request) {
        // 1. 尝试从缓存获取（同一简历+同一JD）
        String jdHash = cacheService.generateJDHash(request.getJobDescription());
        Object cached = cacheService.getJobMatch(resume.getId(), jdHash);
        if (cached != null) {
            log.info("命中岗位匹配缓存: resumeId={}, jdHash={}", resume.getId(), jdHash);
            return (MatchResult) cached;
        }

        MatchResult result = new MatchResult();
        result.setResumeId(resume.getId());
        result.setJobId(request.getJobId());
        result.setJobTitle(request.getJobTitle());
        result.setCompany(request.getCompany());
        
        try {
            // 1. 解析简历
            var parseResult = resumeParser.parseToStructure(resumeContent);
            List<String> resumeSkills = parseResult.getSkills();
            
            // 2. 提取 JD 中的技能
            List<String> jdSkills = extractSkillsFromJD(request.getJobDescription());
            
            // 3. 计算匹配度
            List<String> matchedSkills = new ArrayList<>();
            List<String> missingSkills = new ArrayList<>();
            
            for (String skill : jdSkills) {
                boolean found = resumeSkills.stream()
                    .anyMatch(s -> s.equalsIgnoreCase(skill) || skill.toLowerCase().contains(s.toLowerCase()));
                if (found) {
                    matchedSkills.add(skill);
                } else {
                    missingSkills.add(skill);
                }
            }
            
            // 计算各项得分
            BigDecimal skillScore = BigDecimal.valueOf(jdSkills.isEmpty() ? 100 : 
                matchedSkills.size() * 100.0 / jdSkills.size());
            BigDecimal experienceScore = BigDecimal.valueOf(80); // 简化处理
            BigDecimal educationScore = BigDecimal.valueOf(85); // 简化处理
            
            // 总体得分
            BigDecimal overallScore = skillScore.multiply(BigDecimal.valueOf(0.5))
                .add(experienceScore.multiply(BigDecimal.valueOf(0.3)))
                .add(educationScore.multiply(BigDecimal.valueOf(0.2)))
                .setScale(2, RoundingMode.HALF_UP);
            
            result.setMatchScore(overallScore);
            result.setSkillScore(skillScore);
            result.setExperienceScore(experienceScore);
            result.setEducationScore(educationScore);
            result.setMatchedSkills(matchedSkills);
            result.setMissingSkills(missingSkills);
            
            // 4. 生成改进建议
            StringBuilder improvements = new StringBuilder();
            if (!missingSkills.isEmpty()) {
                improvements.append("建议补充以下技能: ").append(String.join(", ", missingSkills)).append("\n");
            }
            improvements.append("建议针对岗位要求优化简历内容，突出相关经验");
            result.setSuggestedImprovements(improvements.toString());
            
            // 5. 调用 AI 生成更详细的分析
            String prompt = buildMatchAnalysisPrompt(resumeContent, request.getJobDescription());
            String aiResponse = callAi(prompt);
            Map<String, Object> analysis = parseAiResponse(aiResponse);
            
            result.setOverallAnalysis((String) analysis.getOrDefault("analysis", ""));
            if (analysis.containsKey("suggestedImprovements")) {
                result.setSuggestedImprovements((String) analysis.get("suggestedImprovements"));
            }
            
            // 6. 保存匹配分析记录
            saveMatchAnalysis(resume.getId(), request.getJobId(), overallScore, 
                matchedSkills, missingSkills, result.getSuggestedImprovements());
            
            // 缓存匹配结果
            cacheService.cacheJobMatch(resume.getId(), jdHash, result);
            
        } catch (Exception e) {
            log.error("岗位匹配失败", e);
            result.setMatchScore(BigDecimal.valueOf(60));
            result.setOverallAnalysis("匹配服务暂时不可用");
        }
        
        return result;
    }

    @Override
    public List<MatchResult> matchWithJobs(Resume resume, String resumeContent, List<MatchRequest> requests) {
        List<MatchResult> results = new ArrayList<>();
        for (MatchRequest request : requests) {
            results.add(matchWithJob(resume, resumeContent, request));
        }
        // 按匹配度排序
        results.sort((a, b) -> b.getMatchScore().compareTo(a.getMatchScore()));
        return results;
    }

    // ==================== 私有方法 ====================

    private String buildAnalysisPrompt(String resumeContent) {
        return """
            请分析以下简历，提取关键信息并给出评估和建议。

            简历内容:
            %s

            请以JSON格式返回分析结果:
            {
                "score": 评分(0-100),
                "workExperience": "工作经历总结",
                "education": "教育经历总结", 
                "optimizationSuggestions": "优化建议",
                "interviewQuestions": ["面试题1", "面试题2", "面试题3"]
            }
            """.formatted(resumeContent);
    }

    private String buildOptimizationPrompt(String resumeContent, String jobDescription) {
        return """
            请根据目标岗位要求优化以下简历。

            原始简历:
            %s

            目标岗位描述:
            %s

            请以JSON格式返回优化结果:
            {
                "optimizedResume": "优化后的简历内容",
                "matchScore": 匹配度评分(0-100),
                "optimizationSuggestions": "主要优化建议",
                "interviewQuestions": ["针对该岗位的面试题1", "面试题2", "面试题3"]
            }
            """.formatted(resumeContent, jobDescription);
    }

    private String buildMatchAnalysisPrompt(String resumeContent, String jobDescription) {
        return """
            请分析以下简历与岗位的匹配度。

            简历内容:
            %s

            岗位描述:
            %s

            请以JSON格式返回分析结果:
            {
                "analysis": "总体分析",
                "suggestedImprovements": "改进建议"
            }
            """.formatted(resumeContent, jobDescription);
    }

    private String callAi(String prompt) throws Exception {
        long startTime = System.currentTimeMillis();
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", List.of(
            Map.of("role", "user", "content", prompt)
        ));
        
        HttpResponse response = HttpRequest.post(baseUrl + "/text/chatcompletion_v2")
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .body(objectMapper.writeValueAsString(requestBody))
            .execute();
        
        int duration = (int) (System.currentTimeMillis() - startTime);
        
        if (response.isOk()) {
            Map<String, Object> responseBody = objectMapper.readValue(response.body(),
                new TypeReference<Map<String, Object>>() {});
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            if (choices != null && !choices.isEmpty()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                String content = (String) message.get("content");
                
                // 记录调用日志
                log.info("📊 AI调用成功: duration={}ms, model={}", duration, model);
                
                return content;
            }
        }
        
        log.warn("AI调用失败: {}", response.body());
        return "{}";
    }

    private Map<String, Object> parseAiResponse(String response) {
        try {
            // 尝试提取JSON
            int start = response.indexOf("{");
            int end = response.lastIndexOf("}");
            if (start >= 0 && end >= 0) {
                String json = response.substring(start, end + 1);
                return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
            }
        } catch (Exception e) {
            log.warn("解析AI响应失败: {}", e.getMessage());
        }
        return new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    private List<String> getStringList(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof List) {
            return (List<String>) value;
        }
        return new ArrayList<>();
    }

    private List<String> extractSkillsFromJD(String jobDescription) {
        // 常见技能关键词
        String[] skillKeywords = {
            "Java", "Python", "Go", "C++", "C#", "JavaScript", "TypeScript", "PHP", "Ruby", "Swift", "Kotlin",
            "Spring", "Spring Boot", "Spring Cloud", "Django", "Flask", "Express", "React", "Vue", "Angular", "Next.js",
            "MySQL", "PostgreSQL", "MongoDB", "Redis", "Elasticsearch", "Kafka", "RabbitMQ", "Docker", "Kubernetes",
            "AWS", "Azure", "GCP", "TensorFlow", "PyTorch", "Machine Learning", "NLP", "Git", "Linux", "API", "Microservice"
        };
        
        List<String> skills = new ArrayList<>();
        String lowerDesc = jobDescription.toLowerCase();
        
        for (String skill : skillKeywords) {
            if (lowerDesc.contains(skill.toLowerCase())) {
                skills.add(skill);
            }
        }
        
        return skills;
    }

    @Override
    public Resume getResumeById(Long id) {
        return resumeService.getById(id);
    }

    private void saveMatchAnalysis(Long resumeId, Long jobId, BigDecimal score,
                                   List<String> matchedSkills, List<String> missingSkills,
                                   String suggestions) {
        try {
            MatchAnalysis analysis = new MatchAnalysis();
            analysis.setResumeId(resumeId);
            analysis.setJobId(jobId);
            analysis.setOverallScore(score);
            analysis.setSkillMatchDetails(objectMapper.writeValueAsString(matchedSkills));
            analysis.setMissingSkills(objectMapper.writeValueAsString(missingSkills));
            analysis.setSuggestedImprovements(suggestions);
            
            // 插入或更新
            MatchAnalysis existing = matchAnalysisMapper.selectOne(
                new LambdaQueryWrapper<MatchAnalysis>()
                    .eq(MatchAnalysis::getResumeId, resumeId)
                    .eq(MatchAnalysis::getJobId, jobId)
            );
            
            if (existing != null) {
                analysis.setId(existing.getId());
                matchAnalysisMapper.updateById(analysis);
            } else {
                matchAnalysisMapper.insert(analysis);
            }
        } catch (Exception e) {
            log.error("保存匹配分析失败", e);
        }
    }
}
