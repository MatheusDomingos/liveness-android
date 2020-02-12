package com.acessobio.liveness.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LivenessResult {

    @SerializedName("Error")
    private Error error;
    @SerializedName("Status")
    private int status;
    @SerializedName("Process")
    private Process process;
}
