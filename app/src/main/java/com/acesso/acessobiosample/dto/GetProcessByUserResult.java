package com.acesso.acessobiosample.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetProcessByUserResult {

    @SerializedName("Error")
    private Error error;

    @SerializedName("Process")
    private Process Process;

}
