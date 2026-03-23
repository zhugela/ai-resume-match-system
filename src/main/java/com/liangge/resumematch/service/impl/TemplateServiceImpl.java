package com.liangge.resumematch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liangge.resumematch.entity.ResumeTemplate;
import com.liangge.resumematch.mapper.ResumeTemplateMapper;
import com.liangge.resumematch.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final ResumeTemplateMapper templateMapper;

    @Override
    public List<Map<String, Object>> getAll() {
        return templateMapper.selectList(null).stream()
                .map(this::toMap)
                .collect(Collectors.toList());
    }

    @Override
    public String getDownloadUrl(Long id) {
        ResumeTemplate t = templateMapper.selectById(id);
        if (t != null) {
            t.setDownloadCount(t.getDownloadCount() + 1);
            templateMapper.updateById(t);
        }
        return t != null ? t.getFileUrl() : "";
    }

    private Map<String, Object> toMap(ResumeTemplate t) {
        return Map.of(
            "id", t.getId(),
            "name", t.getName() != null ? t.getName() : "",
            "description", t.getDescription() != null ? t.getDescription() : "",
            "fileType", t.getFileType() != null ? t.getFileType() : "",
            "downloadCount", t.getDownloadCount() != null ? t.getDownloadCount() : 0
        );
    }
}
