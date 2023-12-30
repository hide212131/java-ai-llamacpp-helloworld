package com.example.aillamacpphelloworld;

import de.kherud.llama.InferenceParameters;
import de.kherud.llama.LlamaModel;
import de.kherud.llama.ModelParameters;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.prompt.Prompt;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class LlamaCppChatClient implements ChatClient {

    private String modelHome;

    private String modelName;

    public String getModelHome() {
        return modelHome;
    }

    public void setModelHome(String modelHome) {
        this.modelHome = modelHome;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public ChatResponse generate(Prompt prompt) {
        LlamaModel.setLogger((level, message) -> System.out.print(message));
        var modelParams = new ModelParameters()
                .setNGpuLayers(1);
        var inferParams = new InferenceParameters()
                .setTemperature(0.7f)
                .setPenalizeNl(true)
//                .setNProbs(10)
                .setMirostat(InferenceParameters.MiroStat.V2)
                .setAntiPrompt("User:");

        var modelPath = Path.of(modelHome, modelName).toString();

        var sb = new StringBuilder();
        try (var model = new LlamaModel(modelPath, modelParams)) {
            Iterable<LlamaModel.Output> outputs = model.generate(prompt.getContents(), inferParams);
            for (LlamaModel.Output output : outputs) {
                sb.append(output.text);
            }
        }
        return new ChatResponse(List.of(new Generation(sb.toString())));
    }
}