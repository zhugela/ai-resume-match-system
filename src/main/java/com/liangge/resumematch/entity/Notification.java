package com.liangge.resumematch.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@TableName("t_notification")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    /**
     * 通知类型: SYSTEM-系统通知, MATCH_RESULT-匹配结果, JOB_UPDATE-岗位更新
     */
    private String type;
    
    private String title;
    
    private String content;
    
    /**
     * 是否已读: 0-未读, 1-已读
     */
    private Integer isRead;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
