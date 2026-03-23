package com.liangge.resumematch.service;

import com.liangge.resumematch.dto.BatchMatchRequest;
import com.liangge.resumematch.dto.BatchMatchResult;

/**
 * 批量匹配服务
 */
public interface BatchMatchService {

    /**
     * 批量匹配简历与多个岗位
     *
     * @param request 批量匹配请求
     * @return 批量匹配结果
     */
    BatchMatchResult batchMatch(BatchMatchRequest request);
}
