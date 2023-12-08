package com.example.nstunews;

public class dataTag {
    String newsId;
    String category;

    public dataTag(){}

    public dataTag(String newsId, String category) {
        this.newsId = newsId;
        this.category = category;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
