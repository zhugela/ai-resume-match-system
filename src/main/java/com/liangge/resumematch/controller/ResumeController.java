package com.liangge.resumematch.controller;

import com.liangge.resumematch.common.Result;
import com.liangge.resumematch.entity.Resume;
import com.liangge.resumematch.entity.User;
import com.liangge.resumematch.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/upload")
    public Result<Resume> uploadResume(@RequestParam("file") MultipartFile file,
                                        Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            Resume resume = resumeService.uploadResume(userId, file.getOriginalFilename(), file.getBytes());
            return Result.success(resume);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<Resume>> list(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        List<Resume> resumes = resumeService.lambdaQuery()
                .eq(Resume::getUserId, userId)
                .orderByDesc(Resume::getCreatedAt)
                .list();
        return Result.success(resumes);
    }

    @GetMapping("/{id}")
    public Result<Resume> getById(@PathVariable Long id, Authentication authentication) {
        // 权限检查：只能查看自己的简历
        Long userId = getCurrentUserId(authentication);
        Resume resume = resumeService.getById(id);
        
        if (resume == null) {
            return Result.error(404, "简历不存在");
        }
        
        // 检查是否是本人
        if (!resume.getUserId().equals(userId)) {
            return Result.error(403, "无权限访问");
        }
        
        return Result.success(resume);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, Authentication authentication) {
        // 权限检查：只能删除自己的简历
        Long userId = getCurrentUserId(authentication);
        Resume resume = resumeService.getById(id);
        
        if (resume == null) {
            return Result.error(404, "简历不存在");
        }
        
        // 检查是否是本人
        if (!resume.getUserId().equals(userId)) {
            return Result.error(403, "无权限删除");
        }
        
        resumeService.removeById(id);
        return Result.success();
    }

    @PostMapping("/{id}/analyze")
    public Result<Void> analyze(@PathVariable Long id, Authentication authentication) {
        // 权限检查
        Long userId = getCurrentUserId(authentication);
        Resume resume = resumeService.getById(id);
        
        if (resume == null) {
            return Result.error(404, "简历不存在");
        }
        
        if (!resume.getUserId().equals(userId)) {
            return Result.error(403, "无权限操作");
        }
        
        resumeService.analyzeResume(id);
        return Result.success();
    }

    /**
     * 从SecurityContext获取当前用户ID
     */
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("用户未登录");
        }
        
        if (authentication.getPrincipal() instanceof User user) {
            return user.getId();
        }
        
        throw new RuntimeException("无效的用户信息");
    }
}
