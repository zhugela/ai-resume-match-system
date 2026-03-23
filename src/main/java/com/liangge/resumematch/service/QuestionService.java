package com.liangge.resumematch.service;

import java.util.List;
import java.util.Map;

public interface QuestionService {
    List<Map<String, Object>> search(String keyword, String category, String difficulty);
    Map<String, Object> getById(Long id);
}
