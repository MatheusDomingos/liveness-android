package com.acesso.acessobiosample.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LivenessRequestSample {

    @SerializedName("liveness")
    private LivenessRequest liveness;

}
