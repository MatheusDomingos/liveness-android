package com.example.acessobiosample.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecuteProcessResult {

    @SerializedName("Error")
    private Error error;
    @SerializedName("Status")
    private int status;
}
