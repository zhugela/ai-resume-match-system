package com.liangge.resumematch.controller;

import com.liangge.resumematch.common.Result;
import com.liangge.resumematch.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/template")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        return Result.success(templateService.getAll());
    }

    @GetMapping("/download/{id}")
    public Result<String> download(@PathVariable Long id) {
        return Result.success(templateService.getDownloadUrl(id));
    }
}
