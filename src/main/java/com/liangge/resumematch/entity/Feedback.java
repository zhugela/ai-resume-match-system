package com.liangge.resumematch.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@TableName("t_feedback")
public class Feedback {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String content;
    
    /**
     * 反馈类型: BUG-问题反馈, FEATURE-功能建议, OTHER-其他
     */
    private String type;
    
    /**
     * 处理状态: PENDING-待处理, PROCESSING-处理中, RESOLVED-已解决, REJECTED-已拒绝
     */
    private String status;
    
    /**
     * 管理员回复
     */
    private String reply;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
