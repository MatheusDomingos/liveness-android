package com.acesso.acessobiosample.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LivenessRequest {



    @SerializedName("isLive")
    private Boolean isLIve;

    @SerializedName("Score")
    private Float Score;

    @SerializedName("isLiveClose")
    private Boolean isLiveClose;

    @SerializedName("isLiveAway")
    private Boolean isLiveAway;

    @SerializedName("ScoreClose")
    private Float ScoreClose;

    @SerializedName("ScoreAway")
    private Float ScoreAway;

    @SerializedName("IsBlinking")
    private Boolean IsBlinking;

    @SerializedName("IsSmilling")
    private Boolean IsSmilling;

    @SerializedName("IsResetSession")
    private Boolean IsResetSession;

    @SerializedName("AttemptsValidate")
    private Integer AttemptsValidate;

    @SerializedName("IsResetSessionSpoofing")
    private Boolean IsResetSessionSpoofing;

    @SerializedName("AttemptsSpoofing")
    private Integer AttemptsSpoofing;

    @SerializedName("UserName")
    private String UserName;

    @SerializedName("UserCPF")
    private String UserCPF;

    @SerializedName("DeviceModel")
    private String DeviceModel;

    @SerializedName("TimeTotal")
    private Integer TimeTotal;

    @SerializedName("BiometryStatus")
    private Integer BiometryStatus;

    @SerializedName("BiometryMessage")
    private String BiometryMessage;

    @SerializedName("BiometryStatusAway")
    private Integer BiometryStatusAway;

    @SerializedName("BiometryMessageAway")
    private String BiometryMessageAway;

    @SerializedName("Base64Center")
    private String Base64Center;

    @SerializedName("Base64Away")
    private String Base64Away;

}
