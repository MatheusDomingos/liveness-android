package com.acesso.acessobiosample.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticateResponse {

    @SerializedName("AuthenticateSubjectResult")
    private AuthenticateResult authenticateResult;

    @SerializedName("Error")
    private Error error;

    @SerializedName("Process")
    private Error process;

    public boolean isValid() {
        return error == null && authenticateResult != null && authenticateResult.getError() == null && authenticateResult.getStatus() == 1;
    }

    public String getMessageError() {
        String message = "";

        if (error != null) {
            message = error.getDescription();
        }

        if (authenticateResult != null && authenticateResult.getError() != null) {
            message = authenticateResult.getError().getDescription();
        }

        if (message == null || message.isEmpty()) {
            message = "Erro ao autenticar face";
        }

        return message;
    }
}
