package com.liangge.resumematch.controller;

import com.liangge.resumematch.common.Result;
import com.liangge.resumematch.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/salary")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String location) {
        return Result.success(salaryService.search(company, position, location));
    }
}
