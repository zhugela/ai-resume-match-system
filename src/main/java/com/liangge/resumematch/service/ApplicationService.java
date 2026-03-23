package com.liangge.resumematch.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liangge.resumematch.entity.ApplicationProgress;

import java.util.Map;

public interface ApplicationService {
    Page<Map<String, Object>> getUserApplications(Long userId, Integer pageNum, Integer pageSize);
    void addApplication(Long userId, ApplicationProgress app);
    void updateApplication(Long userId, Long id, ApplicationProgress app);
    void deleteApplication(Long userId, Long id);
}
