package com.example.acessobiosample.dto;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Process {


    @SerializedName("Id")
    private String id;

    @SerializedName("Score")
    private float Score;

    @SerializedName("Status")
    private int Status;

    @SerializedName("Subject")
    private Subject Subject;

}
