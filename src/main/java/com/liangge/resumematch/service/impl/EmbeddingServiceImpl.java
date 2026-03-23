package com.liangge.resumematch.service.impl;

import com.liangge.resumematch.service.EmbeddingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 向量嵌入服务实现 - 使用 Spring AI
 */
@Slf4j
@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private final EmbeddingModel embeddingModel;

    public EmbeddingServiceImpl(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    /**
     * 将文本转换为向量
     */
    @Override
    public List<Float> embed(String text) {
        try {
            EmbeddingRequest request = new EmbeddingRequest(List.of(text), null);

            EmbeddingResponse response = embeddingModel.call(request);

            if (response != null && response.getResults() != null && !response.getResults().isEmpty()) {
                float[] embedding = response.getResults().get(0).getOutput();
                List<Float> result = new ArrayList<>();
                for (float f : embedding) {
                    result.add(f);
                }
                return result;
            }
        } catch (Exception e) {
            log.error("向量嵌入失败: {}", e.getMessage());
        }

        return new ArrayList<>();
    }

    /**
     * 批量将文本转换为向量
     */
    @Override
    public List<List<Float>> embedBatch(List<String> texts) {
        try {
            EmbeddingRequest request = new EmbeddingRequest(texts, null);

            EmbeddingResponse response = embeddingModel.call(request);

            if (response != null && response.getResults() != null && !response.getResults().isEmpty()) {
                List<List<Float>> results = new ArrayList<>();
                for (var embedding : response.getResults()) {
                    float[] output = embedding.getOutput();
                    List<Float> result = new ArrayList<>();
                    for (float f : output) {
                        result.add(f);
                    }
                    results.add(result);
                }
                return results;
            }
        } catch (Exception e) {
            log.error("批量向量嵌入失败: {}", e.getMessage());
        }

        return new ArrayList<>();
    }
}
