package com.liangge.resumematch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liangge.resumematch.entity.ApplicationProgress;
import com.liangge.resumematch.mapper.ApplicationProgressMapper;
import com.liangge.resumematch.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationProgressMapper applicationMapper;

    @Override
    public Page<Map<String, Object>> getUserApplications(Long userId, Integer pageNum, Integer pageSize) {
        Page<ApplicationProgress> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ApplicationProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApplicationProgress::getUserId, userId)
               .orderByDesc(ApplicationProgress::getCreatedAt);

        Page<ApplicationProgress> result = applicationMapper.selectPage(page, wrapper);

        // 转换为 Map
        Page<Map<String, Object>> mapPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        mapPage.setRecords(result.getRecords().stream().map(this::toMap).toList());
        return mapPage;
    }

    @Override
    public void addApplication(Long userId, ApplicationProgress app) {
        ApplicationProgress a = new ApplicationProgress();
        a.setUserId(userId);
        a.setCompany(app.getCompany());
        a.setPosition(app.getPosition());
        a.setStatus(app.getStatus());
        a.setSalary(app.getSalary());
        a.setNotes(app.getNotes());
        applicationMapper.insert(a);
    }

    @Override
    public void updateApplication(Long userId, Long id, ApplicationProgress app) {
        ApplicationProgress a = applicationMapper.selectById(id);
        if (a != null && a.getUserId().equals(userId)) {
            if (app.getCompany() != null) a.setCompany(app.getCompany());
            if (app.getPosition() != null) a.setPosition(app.getPosition());
            if (app.getStatus() != null) a.setStatus(app.getStatus());
            if (app.getSalary() != null) a.setSalary(app.getSalary());
            if (app.getNotes() != null) a.setNotes(app.getNotes());
            applicationMapper.updateById(a);
        }
    }

    @Override
    public void deleteApplication(Long userId, Long id) {
        ApplicationProgress a = applicationMapper.selectById(id);
        if (a != null && a.getUserId().equals(userId)) {
            applicationMapper.deleteById(id);
        }
    }

    private Map<String, Object> toMap(ApplicationProgress a) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", a.getId());
        map.put("company", a.getCompany());
        map.put("position", a.getPosition());
        map.put("status", a.getStatus());
        map.put("salary", a.getSalary());
        map.put("notes", a.getNotes());
        map.put("createdAt", a.getCreatedAt());
        return map;
    }
}
