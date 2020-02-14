package com.acesso.acessobiosample.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LivenessBillingResponse {

    @SerializedName("Status")
    private boolean Status;

    @SerializedName("Error")
    private Error error;

    @SerializedName("Process")
    private Error process;

    public boolean isValid() {
        return error == null;
    }

    public String getMessageError() {
        String message = "";

        if (error != null) {
            message = error.getDescription();
        }

        if (message == null || message.isEmpty()) {
            message = "Erro ao inserir face";
        }

        return message;
    }
}
