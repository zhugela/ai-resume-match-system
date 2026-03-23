package com.liangge.resumematch.service;

import java.util.List;

/**
 * 向量嵌入服务 - 文本转向量
 */
public interface EmbeddingService {

    /**
     * 将文本转换为向量
     *
     * @param text 文本内容
     * @return 向量数组
     */
    List<Float> embed(String text);

    /**
     * 批量将文本转换为向量
     *
     * @param texts 文本列表
     * @return 向量列表
     */
    List<List<Float>> embedBatch(List<String> texts);
}
