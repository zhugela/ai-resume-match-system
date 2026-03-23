package com.liangge.resumematch.dto;

import lombok.Data;

@Data
public class JobPublishRequest {
    private String title;
    private String company;
    private String location;
    private String salaryRange;
    private String jobType;
    private String description;
    private String requirements;
    private String[] requiredSkills;
    private String[] preferredSkills;
}
