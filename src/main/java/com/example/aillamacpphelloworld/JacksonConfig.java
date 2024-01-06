package com.example.aillamacpphelloworld;

import com.example.openapi.model.ChatCompletionRequestMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ChatCompletionRequestMessage.class, new ChatCompletionRequestMessageDeserializer());
        mapper.registerModule(module);
        return mapper;
    }

    @Bean
    public JsonNullableModule jsonNullableModule(ObjectMapper objectMapper) {
        var module=new JsonNullableModule();
        objectMapper.registerModule(module);
        return module;
    }
}
