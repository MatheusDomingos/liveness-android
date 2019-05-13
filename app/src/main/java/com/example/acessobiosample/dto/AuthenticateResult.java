package com.example.acessobiosample.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticateResult {

    @SerializedName("Error")
    private Error error;
    @SerializedName("Status")
    private int status;
    @SerializedName("Score")
    private float score;
}
