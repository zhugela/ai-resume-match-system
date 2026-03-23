package com.liangge.resumematch.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class StatisticsDTO {
    /** 用户总数 */
    private Long userCount;
    /** 简历总数 */
    private Long resumeCount;
    /** 岗位总数 */
    private Long jobCount;
    /** 匹配总次数 */
    private Long matchCount;
    /** 匹配成功率 */
    private Double matchSuccessRate;
    /** 热门岗位TOP10 */
    private List<HotJobDTO> hotJobs;
    /** 每日趋势 */
    private List<DailyTrendDTO> dailyTrend;
    
    @Data
    public static class HotJobDTO {
        private Long jobId;
        private String title;
        private String company;
        private Long matchCount;
        private Double avgScore;
    }
    
    @Data
    public static class DailyTrendDTO {
        private String date;
        private Long userCount;
        private Long resumeCount;
        private Long matchCount;
    }
}
