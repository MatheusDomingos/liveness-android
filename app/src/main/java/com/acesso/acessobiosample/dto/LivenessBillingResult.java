package com.acesso.acessobiosample.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LivenessBillingResult {

    @SerializedName("Error")
    private Error error;
    @SerializedName("Status")
    private int status;
}
