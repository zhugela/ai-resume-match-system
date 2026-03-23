package com.liangge.resumematch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liangge.resumematch.entity.Resume;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ResumeMapper extends BaseMapper<Resume> {
}
