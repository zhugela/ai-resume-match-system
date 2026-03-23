package com.liangge.resumematch.controller;

import com.liangge.resumematch.common.Result;
import com.liangge.resumematch.entity.ExportRecord;
import com.liangge.resumematch.entity.User;
import com.liangge.resumematch.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    /**
     * 导出简历对比报告
     */
    @PostMapping("/comparison/{resumeId}")
    public Result<ExportRecord> exportComparison(
            @PathVariable Long resumeId,
            @RequestParam(required = false) Long versionId,
            @RequestParam(defaultValue = "WORD") String type,
            Authentication authentication) {
        
        try {
            Long userId = getUserId(authentication);
            ExportRecord record = exportService.exportComparison(resumeId, versionId, type);
            return Result.success(record);
        } catch (Exception e) {
            return Result.error("导出失败: " + e.getMessage());
        }
    }

    /**
     * 下载导出的文件
     */
    @GetMapping("/download/{exportId}")
    public ResponseEntity<Resource> download(@PathVariable Long exportId, Authentication authentication) {
        Long userId = getUserId(authentication);
        
        ExportRecord record = exportService.getById(exportId);
        if (record == null || record.getFileUrl() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Resource resource = new FileSystemResource(record.getFileUrl());
            
            String contentType = "application/octet-stream";
            if (record.getFileName() != null && record.getFileName().endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (record.getFileName() != null && record.getFileName().endsWith(".docx")) {
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + record.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取导出记录列表
     */
    @GetMapping("/list/{resumeId}")
    public Result<List<ExportRecord>> getExportList(@PathVariable Long resumeId, Authentication authentication) {
        Long userId = getUserId(authentication);
        List<ExportRecord> records = exportService.getExportsByResumeId(resumeId);
        return Result.success(records);
    }

    /**
     * 从SecurityContext获取当前用户ID
     */
    private Long getUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("用户未登录");
        }
        
        if (authentication.getPrincipal() instanceof User user) {
            return user.getId();
        }
        
        throw new RuntimeException("无效的用户信息");
    }
}
