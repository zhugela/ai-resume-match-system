package com.liangge.resumematch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI 配置 - 支持多AI提供商
 */
@Slf4j
@Configuration
public class SpringAIConfig {

    @Value("${ai.provider:minimax}")
    private String aiProvider;

    @Value("${ai.openai.api-key:}")
    private String openaiApiKey;

    @Value("${ai.openai.base-url:https://api.openai.com}")
    private String openaiBaseUrl;

    @Value("${ai.openai.model:gpt-4o}")
    private String openaiModel;

    @Value("${ai.openai.embedding-model:text-embedding-ada-002}")
    private String embeddingModel;

    @Value("${ai.zhipu.api-key:}")
    private String zhipuApiKey;

    @Value("${ai.minimax.api-key:}")
    private String minimaxApiKey;

    private OpenAiApi createOpenAiApi() {
        if (aiProvider.equalsIgnoreCase("openai") && !openaiApiKey.isEmpty()) {
            return OpenAiApi.builder()
                    .apiKey(openaiApiKey)
                    .baseUrl(openaiBaseUrl)
                    .build();
        } else if (aiProvider.equalsIgnoreCase("zhipu") && !zhipuApiKey.isEmpty()) {
            return OpenAiApi.builder()
                    .apiKey(zhipuApiKey)
                    .baseUrl("https://open.bigmodel.cn/api/paas/v4")
                    .build();
        } else {
            log.warn("未配置有效的AI提供商，使用默认配置");
            return OpenAiApi.builder()
                    .apiKey(openaiApiKey.isEmpty() ? "sk-placeholder" : openaiApiKey)
                    .baseUrl(openaiBaseUrl)
                    .build();
        }
    }

    /**
     * OpenAI ChatModel 配置
     */
    @Bean
    public ChatModel openAiChatModel() {
        log.info("初始化 Spring AI ChatModel，提供商: {}", aiProvider);

        OpenAiApi api = createOpenAiApi();

        if (aiProvider.equalsIgnoreCase("zhipu") && !zhipuApiKey.isEmpty()) {
            return OpenAiChatModel.builder()
                    .openAiApi(api)
                    .defaultOptions(OpenAiChatOptions.builder()
                            .model("glm-4")
                            .temperature(0.7)
                            .build())
                    .build();
        }

        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(openaiModel)
                        .temperature(0.7)
                        .build())
                .build();
    }

    /**
     * OpenAI EmbeddingModel 配置
     */
    @Bean
    public EmbeddingModel openAiEmbeddingModel() {
        log.info("初始化 Spring AI EmbeddingModel，提供商: {}", aiProvider);

        OpenAiApi api = createOpenAiApi();

        return new OpenAiEmbeddingModel(api);
    }

    /**
     * ChatClient Bean - 用于简化AI调用
     */
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("你是一个专业的简历分析助手，擅长提取简历中的关键信息，包括技能、工作经历、教育背景等。")
                .build();
    }
}
