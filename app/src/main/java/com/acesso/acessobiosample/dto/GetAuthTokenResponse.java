package com.acesso.acessobiosample.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAuthTokenResponse {

    @SerializedName("GetAuthTokenResult")
    private GetAuthTokenResult getAuthTokenResult;

    @SerializedName("Token")
    private String Token;

    @SerializedName("Error")
    private Error error;

    public boolean isValid() {
        return error == null && getToken() != null;
    }

    public String getMessageError() {
        String message = "";

        if (error != null) {
            message = error.getDescription();
        }

        if (getAuthTokenResult != null && getAuthTokenResult.getError() != null) {
            message = getAuthTokenResult.getError().getDescription();
        }

        if (message == null || message.isEmpty()) {
            message = "Erro ao recuperar token";
        }

        return message;
    }
}
