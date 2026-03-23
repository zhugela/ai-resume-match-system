package com.liangge.resumematch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liangge.resumematch.dto.JobPublishRequest;
import com.liangge.resumematch.entity.Job;
import com.liangge.resumematch.mapper.JobMapper;
import com.liangge.resumematch.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Job publish(Long hrId, JobPublishRequest request) {
        Job job = new Job();
        job.setHrId(hrId);
        job.setTitle(request.getTitle());
        job.setCompany(request.getCompany());
        job.setLocation(request.getLocation());
        job.setSalaryRange(request.getSalaryRange());
        job.setJobType(request.getJobType());
        job.setDescription(request.getDescription());
        job.setRequirements(request.getRequirements());
        
        try {
            if (request.getRequiredSkills() != null) {
                job.setRequiredSkills(objectMapper.writeValueAsString(request.getRequiredSkills()));
            }
            if (request.getPreferredSkills() != null) {
                job.setPreferredSkills(objectMapper.writeValueAsString(request.getPreferredSkills()));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("技能数据转换失败");
        }
        
        job.setStatus("OPEN");
        job.setViews(0);
        
        save(job);
        return job;
    }

    @Override
    public Page<Job> list(Integer pageNum, Integer pageSize, String keyword, String location) {
        Page<Job> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Job> query = new QueryWrapper<>();

        query.eq("status", "OPEN");

        if (keyword != null && !keyword.isEmpty()) {
            query.and(w -> w.like("title", keyword)
                    .or().like("company", keyword)
                    .or().like("description", keyword));
        }

        if (location != null && !location.isEmpty()) {
            query.eq("location", location);
        }

        query.orderByDesc("created_at");

        return baseMapper.selectPage(page, query);
    }

    @Override
    public Job getById(Long id) {
        Job job = baseMapper.selectById(id);
        if (job != null) {
            // 增加浏览次数
            lambdaUpdate()
                    .eq(Job::getId, id)
                    .setSql("views = views + 1")
                    .update();
        }
        return job;
    }

    @Override
    public Job update(Long id, Long hrId, JobPublishRequest request) {
        Job job = getById(id);
        if (job == null) {
            throw new RuntimeException("岗位不存在");
        }
        if (!job.getHrId().equals(hrId)) {
            throw new RuntimeException("无权限操作");
        }
        
        job.setTitle(request.getTitle());
        job.setCompany(request.getCompany());
        job.setLocation(request.getLocation());
        job.setSalaryRange(request.getSalaryRange());
        job.setJobType(request.getJobType());
        job.setDescription(request.getDescription());
        job.setRequirements(request.getRequirements());
        
        try {
            if (request.getRequiredSkills() != null) {
                job.setRequiredSkills(objectMapper.writeValueAsString(request.getRequiredSkills()));
            }
            if (request.getPreferredSkills() != null) {
                job.setPreferredSkills(objectMapper.writeValueAsString(request.getPreferredSkills()));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("技能数据转换失败");
        }
        
        updateById(job);
        return job;
    }

    @Override
    public void delete(Long id, Long hrId) {
        Job job = getById(id);
        if (job == null) {
            throw new RuntimeException("岗位不存在");
        }
        if (!job.getHrId().equals(hrId)) {
            throw new RuntimeException("无权限操作");
        }
        
        job.setStatus("CLOSED");
        updateById(job);
    }
}
