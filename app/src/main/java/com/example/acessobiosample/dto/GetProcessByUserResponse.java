package com.example.acessobiosample.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetProcessByUserResponse {

    @SerializedName("GetProcessByUserResult")
    private ArrayList<GetProcessByUserResult> getProcessByUserResults;

    @SerializedName("Error")
    private Error error;

    public boolean isValid() {
        return error == null && getProcessByUserResults != null;
    }

    public String getMessageError() {
        String message = "";

        if (error != null) {
            message = error.getDescription();
        }

        if (getProcessByUserResults != null) {
            //message = getAuthTokenResult.getError().getDescription();
        }

        if (message == null || message.isEmpty()) {
            message = "Erro ao recuperar token";
        }

        return message;
    }
}
