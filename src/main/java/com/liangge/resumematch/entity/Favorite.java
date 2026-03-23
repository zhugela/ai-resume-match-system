package com.liangge.resumematch.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@TableName("t_favorite")
public class Favorite {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long jobId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
