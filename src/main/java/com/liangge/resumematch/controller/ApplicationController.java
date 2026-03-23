package com.liangge.resumematch.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liangge.resumematch.common.Result;
import com.liangge.resumematch.entity.ApplicationProgress;
import com.liangge.resumematch.entity.User;
import com.liangge.resumematch.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping("/list")
    public Result<Page<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        return Result.success(applicationService.getUserApplications(userId, pageNum, pageSize));
    }

    @PostMapping
    public Result<Void> add(@RequestBody ApplicationProgress app, Authentication authentication) {
        Long userId = getUserId(authentication);
        applicationService.addApplication(userId, app);
        return Result.success("添加成功");
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ApplicationProgress app, Authentication authentication) {
        Long userId = getUserId(authentication);
        applicationService.updateApplication(userId, id, app);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserId(authentication);
        applicationService.deleteApplication(userId, id);
        return Result.success("删除成功");
    }

    private Long getUserId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof User user) {
            return user.getId();
        }
        throw new RuntimeException("用户未登录");
    }
}
