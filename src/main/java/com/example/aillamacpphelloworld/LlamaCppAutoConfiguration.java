package com.example.aillamacpphelloworld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;

@AutoConfiguration
@EnableConfigurationProperties(LlamaCppProperties.class)
public class LlamaCppAutoConfiguration {

    private final LlamaCppProperties llamaCppProperties;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public LlamaCppAutoConfiguration(com.example.aillamacpphelloworld.LlamaCppProperties llamaCppProperties) {
        this.llamaCppProperties = llamaCppProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public LlamaCppChatClient llamaCppChatClient() {
        LlamaCppChatClient llamaCppChatClient = new LlamaCppChatClient();
        llamaCppChatClient.setModelHome(this.llamaCppProperties.getModelHome());
        llamaCppChatClient.setModelName(this.llamaCppProperties.getModelName());
        logger.info("LlamaCppChatClient created: model={}", Path.of(llamaCppChatClient.getModelHome(), llamaCppChatClient.getModelName()));
        return llamaCppChatClient;
    }

}
