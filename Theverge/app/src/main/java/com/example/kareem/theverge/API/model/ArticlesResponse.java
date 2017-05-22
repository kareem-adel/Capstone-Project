package com.example.kareem.theverge.API.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ArticlesResponse {
    @SerializedName("status")
    public String status;

    @SerializedName("source")
    public String source;

    @SerializedName("sortBy")
    public String sortBy;

    @SerializedName("articles")
    public ArrayList<Article> articles;
}
