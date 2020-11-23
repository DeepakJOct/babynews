package com.originprogrammers.babynews;

public class FlipViewModel {
    String sourceType;
    int imgId;
    String videoUri;
    String text, news;
    String imageUrl;
    String newsSource, publishedOn, newsUrl;

    public FlipViewModel() {
    }

    public FlipViewModel(String sourceType, int imgId, String videoUri, String text, String news, String imageUrl) {
        this.sourceType = sourceType;
        this.imgId = imgId;
        this.text = text;
        this.videoUri = videoUri;
        this.news = news;
        this.imageUrl = imageUrl;
    }

    public FlipViewModel(String sourceType,
                         int imgId,
                         String videoUri,
                         String text,
                         String news,
                         String imageUrl,
                         String newsSource,
                         String publishedOn,
                         String newsUrl) {
        this.sourceType = sourceType;
        this.imgId = imgId;
        this.text = text;
        this.videoUri = videoUri;
        this.news = news;
        this.imageUrl = imageUrl;
        this.newsSource = newsSource;
        this.publishedOn = publishedOn;
        this.newsUrl = newsUrl;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    public String getNewsSource() {
        return newsSource;
    }

    public void setNewsSource(String newsSource) {
        this.newsSource = newsSource;
    }

    public String getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(String publishedOn) {
        this.publishedOn = publishedOn;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
