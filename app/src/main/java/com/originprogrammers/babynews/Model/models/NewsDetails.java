package com.originprogrammers.babynews.Model.models;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class NewsDetails {

    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("url")
    private String url;

    @SerializedName("description")
    private String description;

    @SerializedName("summarization")
    private String summarization;

    @SerializedName("image")
    private String imageUrl;

    @SerializedName("source")
    private String source;

    @SerializedName("publishedDate")
    private String publishedDate;


}
