package com.liangge.resumematch.service;

import java.util.List;
import java.util.Map;

public interface TemplateService {
    List<Map<String, Object>> getAll();
    String getDownloadUrl(Long id);
}
