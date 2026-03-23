package com.liangge.resumematch.service.impl;

import com.liangge.resumematch.dto.SemanticMatchRequest;
import com.liangge.resumematch.dto.SemanticMatchResult;
import com.liangge.resumematch.entity.Job;
import com.liangge.resumematch.entity.Resume;
import com.liangge.resumematch.mapper.JobMapper;
import com.liangge.resumematch.mapper.ResumeMapper;
import com.liangge.resumematch.service.EmbeddingService;
import com.liangge.resumematch.service.SemanticMatchService;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 语义匹配服务实现 - 基于 Qdrant 向量数据库
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SemanticMatchServiceImpl implements SemanticMatchService {

    private final QdrantClient qdrantClient;
    private final EmbeddingService embeddingService;
    private final ResumeMapper resumeMapper;
    private final JobMapper jobMapper;

    @Value("${qdrant.collection-name:resume-match-vectors}")
    private String collectionName;

    /**
     * 简历与岗位的语义匹配
     */
    @Override
    public List<SemanticMatchResult> matchResumeWithJobs(SemanticMatchRequest request) {
        List<SemanticMatchResult> results = new ArrayList<>();

        try {
            // 1. 获取简历向量
            List<Float> resumeVector = getResumeVector(request);

            // 2. 获取岗位向量并计算相似度
            List<Job> jobs = getJobs(request.getJobIds());

            for (Job job : jobs) {
                List<Float> jobVector = embedJob(job);
                double similarity = cosineSimilarity(resumeVector, jobVector);

                SemanticMatchResult result = SemanticMatchResult.builder()
                        .jobId(job.getId())
                        .jobTitle(job.getTitle())
                        .company(job.getCompany())
                        .similarityScore(similarity)
                        .matchReason(generateMatchReason(job, similarity))
                        .build();

                results.add(result);
            }

            // 3. 按相似度排序
            results.sort((a, b) -> Double.compare(b.getSimilarityScore(), a.getSimilarityScore()));

            // 4. 返回 topK
            int topK = request.getTopK() != null ? request.getTopK() : 5;
            if (results.size() > topK) {
                results = results.subList(0, topK);
            }

        } catch (Exception e) {
            log.error("语义匹配失败: {}", e.getMessage());
        }

        return results;
    }

    /**
     * 单个简历与单个岗位的语义匹配
     */
    @Override
    public SemanticMatchResult matchSingle(Long resumeId, Long jobId) {
        try {
            Resume resume = resumeMapper.selectById(resumeId);
            Job job = jobMapper.selectById(jobId);

            if (resume == null || job == null) {
                log.warn("简历或岗位不存在: resumeId={}, jobId={}", resumeId, jobId);
                return null;
            }

            // 获取向量
            List<Float> resumeVector = embedResume(resume);
            List<Float> jobVector = embedJob(job);

            // 计算相似度
            double similarity = cosineSimilarity(resumeVector, jobVector);

            return SemanticMatchResult.builder()
                    .jobId(job.getId())
                    .jobTitle(job.getTitle())
                    .company(job.getCompany())
                    .similarityScore(similarity)
                    .matchReason(generateMatchReason(job, similarity))
                    .build();

        } catch (Exception e) {
            log.error("单个语义匹配失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取简历向量
     */
    private List<Float> getResumeVector(SemanticMatchRequest request) {
        if (request.getResumeText() != null && !request.getResumeText().isEmpty()) {
            return embeddingService.embed(request.getResumeText());
        }

        // 从数据库获取
        Resume resume = resumeMapper.selectById(request.getResumeId());
        if (resume == null) {
            return embeddingService.embed("");
        }

        return embedResume(resume);
    }

    /**
     * 嵌入简历内容
     */
    private List<Float> embedResume(Resume resume) {
        StringBuilder text = new StringBuilder();
        
        if (resume.getSkills() != null) {
            text.append("技能: ").append(resume.getSkills()).append(" ");
        }
        if (resume.getWorkExperience() != null) {
            text.append("工作经历: ").append(resume.getWorkExperience()).append(" ");
        }
        if (resume.getEducation() != null) {
            text.append("教育背景: ").append(resume.getEducation()).append(" ");
        }
        if (resume.getParsedData() != null) {
            text.append(resume.getParsedData());
        }

        return embeddingService.embed(text.toString());
    }

    /**
     * 嵌入岗位内容
     */
    private List<Float> embedJob(Job job) {
        StringBuilder text = new StringBuilder();
        
        if (job.getTitle() != null) {
            text.append("职位: ").append(job.getTitle()).append(" ");
        }
        if (job.getDescription() != null) {
            text.append("岗位职责: ").append(job.getDescription()).append(" ");
        }
        if (job.getRequirements() != null) {
            text.append("岗位要求: ").append(job.getRequirements()).append(" ");
        }
        if (job.getRequiredSkills() != null) {
            text.append("必备技能: ").append(job.getRequiredSkills()).append(" ");
        }
        if (job.getPreferredSkills() != null) {
            text.append("加分技能: ").append(job.getPreferredSkills());
        }

        return embeddingService.embed(text.toString());
    }

    /**
     * 获取岗位列表
     */
    private List<Job> getJobs(List<Long> jobIds) {
        if (jobIds == null || jobIds.isEmpty()) {
            return new ArrayList<>();
        }
        return jobMapper.selectBatchIds(jobIds);
    }

    /**
     * 余弦相似度计算
     */
    private double cosineSimilarity(List<Float> vec1, List<Float> vec2) {
        if (vec1 == null || vec2 == null || vec1.isEmpty() || vec2.isEmpty()) {
            return 0.0;
        }

        if (vec1.size() != vec2.size()) {
            log.warn("向量维度不匹配: {} vs {}", vec1.size(), vec2.size());
            return 0.0;
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < vec1.size(); i++) {
            double v1 = vec1.get(i);
            double v2 = vec2.get(i);
            dotProduct += v1 * v2;
            norm1 += v1 * v1;
            norm2 += v2 * v2;
        }

        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * 生成匹配原因
     */
    private String generateMatchReason(Job job, double similarity) {
        if (similarity >= 0.8) {
            return "高度匹配！您的技能与该岗位要求非常契合";
        } else if (similarity >= 0.6) {
            return "较好匹配，您的背景与该岗位较为契合";
        } else if (similarity >= 0.4) {
            return "中等匹配，建议提升相关技能";
        } else {
            return "匹配度较低，建议查看其他更合适的岗位";
        }
    }
}
