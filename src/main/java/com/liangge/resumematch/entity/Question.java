package com.liangge.resumematch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_question")
public class Question {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String category;
    private String difficulty;
    private String question;
    private String answer;
    private String tags;
    private Integer views;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
