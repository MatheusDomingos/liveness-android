package com.acesso.acessobiosample.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecuteProcessResponse {

    @SerializedName("ExecuteProcessResult")
    private ExecuteProcessResult executeProcessResult;

    @SerializedName("Error")
    private Error error;

    public boolean isValid() {
        return error == null && executeProcessResult != null && executeProcessResult.getError() == null && executeProcessResult.getStatus() == 1;
    }

    public String getMessageError() {
        String message = "";

        if (error != null) {
            message = error.getDescription();
        }

        if (executeProcessResult != null && executeProcessResult.getError() != null) {
            message = executeProcessResult.getError().getDescription();
        }

        if (message == null || message.isEmpty()) {
            message = "Erro ao executar processo";
        }

        return message;
    }
}
