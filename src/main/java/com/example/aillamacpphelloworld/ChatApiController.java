package com.example.aillamacpphelloworld;

import com.example.openapi.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.prompt.Prompt;
import org.springframework.ai.prompt.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
public class ChatApiController {

    private final LlamaCppChatClient chatClient;

    @Autowired
    public ChatApiController(LlamaCppChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * POST /chat/completions : Creates a model response for the given chat conversation.
     *
     * @param createChatCompletionRequest (required)
     * @return OK (status code 200)
     */
    @Operation(
            operationId = "createChatCompletion",
            summary = "Creates a model response for the given chat conversation.",
            tags = {"Chat"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = CreateChatCompletionResponse.class))
                    })
            },
            security = {
                    @SecurityRequirement(name = "ApiKeyAuth")
            }
    )
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/v1/chat/completions",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE,
            consumes = {"application/json"}
    )

    public Flux<CreateChatCompletionStreamResponse> _createChatCompletion(
            @Parameter(name = "CreateChatCompletionRequest", description = "", required = true) @Valid @RequestBody CreateChatCompletionRequest createChatCompletionRequest
    ) {
        var messages = createChatCompletionRequest.getMessages();
        var message = messages.stream()
                .filter(m -> m instanceof ChatCompletionRequestUserMessage)
                .map(m -> (ChatCompletionRequestUserMessage) m)
                .reduce((first, second) -> second)
                .orElseThrow();

        var prompt = new Prompt(new UserMessage(message.getContent()));
        //var chatResponse = chatClient.generate(prompt);
        Flux<ChatResponse> chatResponseFlux = chatClient.generateStream(prompt);
        var date = System.currentTimeMillis();
        return chatResponseFlux.map(chatResponse -> {
            var responseDelta = new ChatCompletionStreamResponseDelta()
                    .content(chatResponse.getGeneration().getContent())
                    .role(ChatCompletionStreamResponseDelta.RoleEnum.ASSISTANT);
            var choices = new CreateChatCompletionStreamResponseChoicesInner()
                    .index(0L)
                    .finishReason(null)
                    .delta(responseDelta);
            return new CreateChatCompletionStreamResponse(
                    "chatcmpl-123",
                    List.of(choices),
                    date,
                    "gpt-3.5-turbo",
                    CreateChatCompletionStreamResponse.ObjectEnum.CHAT_COMPLETION_CHUNK);
        }).concatWithValues(new CreateChatCompletionStreamResponse(
                "chatcmpl-123",
                List.of(new CreateChatCompletionStreamResponseChoicesInner()
                        .index(0L)
                        .finishReason(CreateChatCompletionStreamResponseChoicesInner.FinishReasonEnum.STOP)
                        .delta(new ChatCompletionStreamResponseDelta())),
                date,
                "gpt-3.5-turbo",
                CreateChatCompletionStreamResponse.ObjectEnum.CHAT_COMPLETION_CHUNK
        ));

    }
}
