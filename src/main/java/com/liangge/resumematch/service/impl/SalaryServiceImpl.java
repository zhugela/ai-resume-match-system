package com.liangge.resumematch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liangge.resumematch.entity.SalaryData;
import com.liangge.resumematch.mapper.SalaryDataMapper;
import com.liangge.resumematch.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryService {

    private final SalaryDataMapper salaryDataMapper;

    @Override
    public List<Map<String, Object>> search(String company, String position, String location) {
        LambdaQueryWrapper<SalaryData> wrapper = new LambdaQueryWrapper<>();
        
        if (company != null && !company.isEmpty()) {
            wrapper.like(SalaryData::getCompany, company);
        }
        if (position != null && !position.isEmpty()) {
            wrapper.like(SalaryData::getPosition, position);
        }
        if (location != null && !location.isEmpty()) {
            wrapper.eq(SalaryData::getLocation, location);
        }
        
        return salaryDataMapper.selectList(wrapper).stream()
                .map(this::toMap)
                .collect(Collectors.toList());
    }

    private Map<String, Object> toMap(SalaryData s) {
        return Map.of(
            "id", s.getId(),
            "company", s.getCompany() != null ? s.getCompany() : "",
            "position", s.getPosition() != null ? s.getPosition() : "",
            "location", s.getLocation() != null ? s.getLocation() : "",
            "experience", s.getExperience() != null ? s.getExperience() : "",
            "salaryLow", s.getSalaryLow() != null ? s.getSalaryLow() : 0,
            "salaryHigh", s.getSalaryHigh() != null ? s.getSalaryHigh() : 0,
            "salaryMid", s.getSalaryMid() != null ? s.getSalaryMid() : 0,
            "sampleCount", s.getSampleCount() != null ? s.getSampleCount() : 0
        );
    }
}
