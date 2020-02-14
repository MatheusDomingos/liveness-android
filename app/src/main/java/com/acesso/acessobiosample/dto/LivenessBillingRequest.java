package com.acesso.acessobiosample.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LivenessBillingRequest {

    @SerializedName("id")
    private String id;

    @SerializedName("status")
    private String status;

}
