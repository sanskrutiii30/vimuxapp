package com.example.vimux.Model;

public class ContentAudio {

    String audioId,audio_url,date,description,publisher,title;
    long listened;

    public ContentAudio() {
    }

    public ContentAudio(String audioId, String audio_url, String date, String description, String publisher, String title, long listened) {
        this.audioId = audioId;
        this.audio_url = audio_url;
        this.date = date;
        this.description = description;
        this.publisher = publisher;
        this.title = title;
        this.listened = listened;
    }

    public String getAudioId() {
        return audioId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    public String getAudio_url() {
        return audio_url;
    }

    public void setAudio_url(String audio_url) {
        this.audio_url = audio_url;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getListened() {
        return listened;
    }

    public void setListened(long listened) {
        this.listened = listened;
    }
}

