package com.acessobio.liveness.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Error {
        
    @SerializedName("Code")
    private int code;

    @SerializedName("Description")
    private String description;

}