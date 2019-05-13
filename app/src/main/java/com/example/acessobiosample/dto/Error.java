package com.example.acessobiosample.dto;

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