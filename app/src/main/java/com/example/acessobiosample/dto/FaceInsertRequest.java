package com.example.acessobiosample.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FaceInsertRequest {

    @SerializedName("imagebase64")
    private String imagebase64;

    @SerializedName("validateLiveness")
    private boolean validateLiveness;

}
