package com.liangge.resumematch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liangge.resumematch.entity.Resume;

public interface ResumeService extends IService<Resume> {
    Resume uploadResume(Long userId, String fileName, byte[] fileContent);
    void analyzeResume(Long resumeId);
}
