package com.example.nstunews;

public class dataNews {
    String newsId;
    String headline;
    String body;
    String newsPic;
    String owner_uid;
    String category;
    String tag;

    public dataNews() {
    }

    public dataNews(String newsId, String headline, String body, String newsPic, String owner_uid, String category, String tag) {
        this.newsId = newsId;
        this.headline = headline;
        this.body = body;
        this.newsPic = newsPic;
        this.owner_uid = owner_uid;
        this.category = category;
        this.tag = tag;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getNewsPic() {
        return newsPic;
    }

    public void setNewsPic(String newsPic) {
        this.newsPic = newsPic;
    }

    public String getOwner_uid() {
        return owner_uid;
    }

    public void setOwner_uid(String owner_uid) {
        this.owner_uid = owner_uid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
