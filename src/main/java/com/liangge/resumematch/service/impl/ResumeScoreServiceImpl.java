package com.liangge.resumematch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liangge.resumematch.dto.ResumeScoreDTO;
import com.liangge.resumematch.entity.Resume;
import com.liangge.resumematch.entity.ResumeScore;
import com.liangge.resumematch.mapper.ResumeMapper;
import com.liangge.resumematch.mapper.ResumeScoreMapper;
import com.liangge.resumematch.service.ResumeScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ResumeScoreServiceImpl implements ResumeScoreService {

    private final ResumeScoreMapper resumeScoreMapper;
    private final ResumeMapper resumeMapper;

    @Override
    public ResumeScoreDTO generateScore(Long resumeId) {
        Resume resume = resumeMapper.selectById(resumeId);
        if (resume == null) {
            throw new RuntimeException("简历不存在");
        }
        
        // 检查是否已有评分
        ResumeScoreDTO existingScore = getScore(resumeId);
        if (existingScore != null) {
            return existingScore;
        }
        
        // 根据简历内容模拟AI评分（实际项目中调用AI接口）
        ResumeScoreDTO dto = generateMockScore(resume);
        
        // 保存评分
        ResumeScore score = new ResumeScore();
        score.setResumeId(resumeId);
        score.setOverallScore(dto.getOverallScore());
        score.setSkillScore(dto.getSkillScore());
        score.setExperienceScore(dto.getExperienceScore());
        score.setEducationScore(dto.getEducationScore());
        score.setRadarData(buildRadarJson(dto.getRadarData()));
        score.setAnalysisDetail(dto.getAnalysisDetail());
        resumeScoreMapper.insert(score);
        
        return dto;
    }

    @Override
    public ResumeScoreDTO getScore(Long resumeId) {
        LambdaQueryWrapper<ResumeScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResumeScore::getResumeId, resumeId)
                .orderByDesc(ResumeScore::getCreateTime)
                .last("LIMIT 1");
        ResumeScore score = resumeScoreMapper.selectOne(wrapper);
        
        if (score == null) {
            return null;
        }
        
        ResumeScoreDTO dto = new ResumeScoreDTO();
        dto.setOverallScore(score.getOverallScore());
        dto.setSkillScore(score.getSkillScore());
        dto.setExperienceScore(score.getExperienceScore());
        dto.setEducationScore(score.getEducationScore());
        dto.setRadarData(parseRadarJson(score.getRadarData()));
        dto.setAnalysisDetail(score.getAnalysisDetail());
        return dto;
    }

    @Override
    public ResumeScoreDTO compareResumes(Long resumeId1, Long resumeId2) {
        ResumeScoreDTO score1 = getScore(resumeId1);
        ResumeScoreDTO score2 = getScore(resumeId2);
        
        // 生成对比数据
        ResumeScoreDTO comparison = new ResumeScoreDTO();
        comparison.setSkillScore(compareValues(score1, score2, "skill"));
        comparison.setExperienceScore(compareValues(score1, score2, "experience"));
        comparison.setEducationScore(compareValues(score1, score2, "education"));
        
        double avg1 = avg(score1.getSkillScore(), score1.getExperienceScore(), score1.getEducationScore());
        double avg2 = avg(score2.getSkillScore(), score2.getExperienceScore(), score2.getEducationScore());
        comparison.setOverallScore(Math.max(avg1, avg2));
        
        return comparison;
    }

    private ResumeScoreDTO generateMockScore(Resume resume) {
        ResumeScoreDTO dto = new ResumeScoreDTO();
        
        // 模拟评分，实际项目中根据简历内容调用AI分析
        double skillScore = 60 + Math.random() * 30;
        double expScore = 50 + Math.random() * 40;
        double eduScore = 55 + Math.random() * 35;
        
        dto.setSkillScore(Math.round(skillScore * 10) / 10.0);
        dto.setExperienceScore(Math.round(expScore * 10) / 10.0);
        dto.setEducationScore(Math.round(eduScore * 10) / 10.0);
        dto.setOverallScore(Math.round((skillScore + expScore + eduScore) / 3 * 10) / 10.0);
        
        // 雷达图数据
        ResumeScoreDTO.RadarData radar = new ResumeScoreDTO.RadarData();
        radar.setSkill(dto.getSkillScore());
        radar.setExperience(dto.getExperienceScore());
        radar.setEducation(dto.getEducationScore());
        radar.setCommunication(60.0 + Math.random() * 30);
        radar.setLeadership(50.0 + Math.random() * 40);
        radar.setInitiative(55.0 + Math.random() * 35);
        dto.setRadarData(radar);
        
        dto.setAnalysisDetail("该简历整体评分为" + dto.getOverallScore() + "分，技能匹配度较高，建议加强项目经验描述。");
        
        return dto;
    }

    private String buildRadarJson(ResumeScoreDTO.RadarData radar) {
        return String.format(
            "{\"skill\":%.1f,\"experience\":%.1f,\"education\":%.1f,\"communication\":%.1f,\"leadership\":%.1f,\"initiative\":%.1f}",
            radar.getSkill(), radar.getExperience(), radar.getEducation(),
            radar.getCommunication(), radar.getLeadership(), radar.getInitiative()
        );
    }

    private ResumeScoreDTO.RadarData parseRadarJson(String json) {
        if (json == null || json.isEmpty()) {
            return new ResumeScoreDTO.RadarData();
        }
        ResumeScoreDTO.RadarData radar = new ResumeScoreDTO.RadarData();
        try {
            // 简单解析JSON
            radar.setSkill(extractJsonValue(json, "skill"));
            radar.setExperience(extractJsonValue(json, "experience"));
            radar.setEducation(extractJsonValue(json, "education"));
            radar.setCommunication(extractJsonValue(json, "communication"));
            radar.setLeadership(extractJsonValue(json, "leadership"));
            radar.setInitiative(extractJsonValue(json, "initiative"));
        } catch (Exception e) {
            // ignore
        }
        return radar;
    }

    private Double extractJsonValue(String json, String key) {
        try {
            String pattern = "\"" + key + "\":";
            int idx = json.indexOf(pattern);
            if (idx >= 0) {
                int start = idx + pattern.length();
                int end = start;
                while (end < json.length()) {
                    char c = json.charAt(end);
                    if (c == ',' || c == '}') break;
                    end++;
                }
                return Double.parseDouble(json.substring(start, end).trim());
            }
        } catch (Exception ignored) {}
        return 0.0;
    }

    private Double compareValues(ResumeScoreDTO s1, ResumeScoreDTO s2, String type) {
        if (s1 == null) return s2 == null ? 0.0 : s2.getOverallScore();
        if (s2 == null) return s1.getOverallScore();
        
        switch (type) {
            case "skill": return Math.max(s1.getSkillScore(), s2.getSkillScore());
            case "experience": return Math.max(s1.getExperienceScore(), s2.getExperienceScore());
            case "education": return Math.max(s1.getEducationScore(), s2.getEducationScore());
            default: return 0.0;
        }
    }

    private double avg(Double... values) {
        double sum = 0;
        int count = 0;
        for (Double v : values) {
            if (v != null) {
                sum += v;
                count++;
            }
        }
        return count > 0 ? sum / count : 0;
    }
}
