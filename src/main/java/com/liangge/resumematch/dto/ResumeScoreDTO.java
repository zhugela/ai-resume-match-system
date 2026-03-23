package com.liangge.resumematch.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ResumeScoreDTO {
    /** 综合评分 0-100 */
    private Double overallScore;
    /** 技能匹配度 */
    private Double skillScore;
    /** 经验匹配度 */
    private Double experienceScore;
    /** 学历匹配度 */
    private Double educationScore;
    /** 雷达图数据 */
    private RadarData radarData;
    /** 详细分析 */
    private String analysisDetail;
    /** 改进建议 */
    private List<String> suggestions;
    
    @Data
    public static class RadarData {
        private Double skill;
        private Double experience;
        private Double education;
        private Double communication;
        private Double leadership;
        private Double initiative;
    }
}
