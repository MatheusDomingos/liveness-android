package com.example.acessobiosample.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FaceInsertResponse {

    @SerializedName("FaceInsertResult")
    private FaceInsertResult faceInsertResult;

    @SerializedName("Error")
    private Error error;

    @SerializedName("Process")
    private Error process;

    public boolean isValid() {
        return error == null && faceInsertResult != null && faceInsertResult.getError() == null && faceInsertResult.getStatus() == 1;
    }

    public String getMessageError() {
        String message = "";

        if (error != null) {
            message = error.getDescription();
        }

        if (faceInsertResult != null && faceInsertResult.getError() != null) {
            message = faceInsertResult.getError().getDescription();
        }

        if (message == null || message.isEmpty()) {
            message = "Erro ao inserir face";
        }

        return message;
    }
}
