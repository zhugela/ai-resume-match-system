package com.liangge.resumematch.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class MatchResult {
    private Long resumeId;
    private Long jobId;
    private String jobTitle;
    private String company;
    private BigDecimal matchScore;
    private BigDecimal skillScore;
    private BigDecimal experienceScore;
    private BigDecimal educationScore;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private String suggestedImprovements;
    private String overallAnalysis;
}
