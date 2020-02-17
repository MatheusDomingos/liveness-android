package com.acesso.acessobiosample.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LivenessResponse {

    @SerializedName("LivenessAppResult")
    private LivenessResult livenessResult;

    @SerializedName("Error")
    private Error error;

    @SerializedName("Process")
    private Error process;

    public boolean isValid() {
        return error == null && livenessResult != null && livenessResult.getError() == null && livenessResult.getStatus() == 1;
    }

    public String getMessageError() {
        String message = "";

        if (error != null) {
            message = error.getDescription();
        }

        if (livenessResult != null && livenessResult.getError() != null) {
            message = livenessResult.getError().getDescription();
        }

        if (message == null || message.isEmpty()) {
            message = "Erro ao inserir face";
        }

        return message;
    }
}
