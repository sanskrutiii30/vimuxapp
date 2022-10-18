package com.example.vimux.Model;

public class Content {
    String videoId,title,date,description,publisher,video_url,video_type;
    long views;

    public Content() {

    }

    public Content(String videoId, String title, String date, String description, String publisher, String video_url,String video_type,long views) {
        this.videoId = videoId;
        this.title = title;
        this.date = date;
        this.description = description;
        this.publisher = publisher;
        this.video_url = video_url;
        this.video_type = video_type;
        this.views = views;

    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public String getVideo_type() {
        return video_type;
    }

    public void setVideo_type(String video_type) {
        this.video_type = video_type;
    }
}

