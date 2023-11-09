package com.example.lab1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NumberToWord implements Serializable {
    @SerializedName("status")
    @Expose
    public int status;
    @SerializedName("str")
    @Expose
    public String str;
    @SerializedName("limit")
    @Expose
    public int limit;
}
