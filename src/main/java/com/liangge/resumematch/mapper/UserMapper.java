package com.liangge.resumematch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liangge.resumematch.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
