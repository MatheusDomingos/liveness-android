package com.acesso.acessobiosample.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAuthTokenResult {

    @SerializedName("Error")
    private Error error;

    @SerializedName("Status")
    private int status;

    @SerializedName("AuthToken")
    private String authToken;

    @SerializedName("RenewAuthToken")
    private String renewAuthToken;

}
