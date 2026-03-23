package com.liangge.resumematch.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liangge.resumematch.dto.JobPublishRequest;
import com.liangge.resumematch.entity.Job;

public interface JobService extends IService<Job> {
    Job publish(Long hrId, JobPublishRequest request);
    Page<Job> list(Integer pageNum, Integer pageSize, String keyword, String location);
    Job getById(Long id);
    Job update(Long id, Long hrId, JobPublishRequest request);
    void delete(Long id, Long hrId);
}
