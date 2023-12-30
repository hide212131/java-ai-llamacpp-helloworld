package com.example.aillamacpphelloworld;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.prompt.Prompt;
import org.springframework.ai.prompt.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class SimpleAiController {

    private final LlamaCppChatClient chatClient;

    @Autowired
    public SimpleAiController(LlamaCppChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/ai/simple")
    public Completion completion(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return new Completion(chatClient.generate(message));
    }

    @GetMapping(value = "/ai/simple-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Completion> completionStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        var prompt = new Prompt(new UserMessage(message));
        Flux<ChatResponse> chatResponseFlux = chatClient.generateStream(prompt);
        return chatResponseFlux
                .map(chatResponse -> new Completion(chatResponse.getGeneration().getContent()));
    }
}