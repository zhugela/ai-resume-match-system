package com.liangge.resumematch.service;

import java.util.List;
import java.util.Map;

public interface SalaryService {
    List<Map<String, Object>> search(String company, String position, String location);
}
