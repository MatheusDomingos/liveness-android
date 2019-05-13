package com.example.acessobiosample.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetSubjectResponse {

    @SerializedName("GetSubjectResult")
    private GetSubjectResult getSubjectResult;

    @SerializedName("Error")
    private Error error;

    public boolean isValid() {
        return getSubjectResult != null && error == null && getSubjectResult.getError() == null;
    }

    public String getMessageError() {
        String message = "";

        if (error != null) {
            message = error.getDescription();
        }

        if (getSubjectResult != null && getSubjectResult.getError() != null) {
            message = getSubjectResult.getError().getDescription();
        }

        if (message == null || message.isEmpty()) {
            message = "Erro ao recuperar pessoa";
        }

        return message;
    }
}
