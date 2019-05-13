package com.example.acessobiosample.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProcessResponse {

    @SerializedName("CreateProcessResult")
    private CreateProcessResult createProcessResult;

    @SerializedName("Error")
    private Error error;

    public boolean isValid() {
        return error == null && createProcessResult != null && createProcessResult.getError() == null && createProcessResult.getStatus() == 1;
    }

    public String getMessageError() {
        String message = "";

        if (error != null) {
            message = error.getDescription();
        }

        if (createProcessResult != null && createProcessResult.getError() != null) {
            message = createProcessResult.getError().getDescription();
        }

        if (message == null || message.isEmpty()) {
            message = "Erro ao gerar registro (create)";
        }

        return message;
    }
}
