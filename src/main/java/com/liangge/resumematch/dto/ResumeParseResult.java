package com.liangge.resumematch.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ResumeParseResult {
    private String name;           // 姓名
    private String phone;          // 电话
    private String email;          // 邮箱
    private String age;            // 年龄
    private String location;       // 所在地
    private List<String> skills;   // 技能列表
    private String workExperience; // 工作经历（文本）
    private String education;      // 教育经历（文本）
    private String summary;       // 个人简介
    private Map<String, Object> rawData; // 原始解析数据
}
