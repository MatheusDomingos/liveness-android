package com.example.acessobiosample.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Document {

    @SerializedName("DocumentId")
    String documentId;
    @SerializedName("DocumentType")
    String documentType;
    @SerializedName("Name")
    String name;
    @SerializedName("URI")
    String uri;
}
