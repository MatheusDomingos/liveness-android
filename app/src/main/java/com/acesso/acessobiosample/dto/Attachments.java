package com.acesso.acessobiosample.dto;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Attachments {

    @SerializedName("Uri")
    private String Uri;

    @SerializedName("Name")
    private String name;

}
