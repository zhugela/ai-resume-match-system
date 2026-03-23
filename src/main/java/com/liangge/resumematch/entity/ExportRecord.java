package com.liangge.resumematch.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_export_record")
public class ExportRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long resumeId;
    
    private Long versionId;
    
    private String exportType;
    
    private String fileUrl;
    
    private String fileName;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
