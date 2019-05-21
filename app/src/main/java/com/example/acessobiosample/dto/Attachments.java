package com.example.acessobiosample.dto;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Attachments {

    @SerializedName("Uri")
    private String Uri;

}
