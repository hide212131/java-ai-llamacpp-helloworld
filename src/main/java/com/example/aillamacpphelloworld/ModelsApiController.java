package com.example.aillamacpphelloworld;

import com.example.openapi.api.ModelsApi;
import com.example.openapi.model.ListModelsResponse;
import com.example.openapi.model.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModelsApiController implements ModelsApi {

    @Override
    @GetMapping("/v1/models")
    public ResponseEntity<ListModelsResponse> listModels() {
        var response = new ListModelsResponse();
        response.addDataItem(new Model("gpt-3.5-turbo", 0L, Model.ObjectEnum.MODEL, "kagamih"));
        return ResponseEntity.ok(response);
    }

}