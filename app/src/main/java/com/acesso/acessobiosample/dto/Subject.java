package com.acesso.acessobiosample.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Subject {

    @SerializedName("Code")
    private String code;
    @SerializedName("Name")
    private String name;
    @SerializedName("Gender")
    private String gender;
    @SerializedName("Email")
    private String email;
    @SerializedName("Phone")
    private String phone;
    @SerializedName("Conflict")
    private Conflict conflict;
    @SerializedName("Documents")
    private List<Document> documents;
}
