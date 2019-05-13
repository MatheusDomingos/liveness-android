package com.example.acessobiosample.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetProcessResult {

    @SerializedName("Error")
    private Error error;

    @SerializedName("Process")
    private Process Process;

}
