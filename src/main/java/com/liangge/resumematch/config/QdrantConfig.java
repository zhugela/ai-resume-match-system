package com.liangge.resumematch.config;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Qdrant 向量数据库配置
 */
@Slf4j
@Configuration
public class QdrantConfig {

    @Value("${qdrant.host:localhost}")
    private String host;

    @Value("${qdrant.port:6333}")
    private int port;

    @Value("${qdrant.api-key:}")
    private String apiKey;

    @Value("${qdrant.collection-name:resume-match-vectors}")
    private String collectionName;

    @Value("${qdrant.vector-size:1536}")
    private int vectorSize;

    /**
     * Qdrant 客户端
     */
    @Bean
    public QdrantClient qdrantClient() {
        log.info("初始化 Qdrant 客户端: {}:{}", host, port);

        QdrantGrpcClient.Builder builder = QdrantGrpcClient.newBuilder(host, port, false);

        if (apiKey != null && !apiKey.isEmpty()) {
            builder.withApiKey(apiKey);
        }

        return new QdrantClient(builder.build());
    }

    /**
     * 获取 Collection 名称
     */
    @Bean
    public String qdrantCollectionName() {
        return collectionName;
    }

    /**
     * 获取向量维度
     */
    @Bean
    public int qdrantVectorSize() {
        return vectorSize;
    }
}
