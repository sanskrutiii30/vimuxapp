package com.example.vimux.Model;

public class Channels {

    String creator,description,joined,logo,name;

    public Channels() {
    }

    public Channels(String creator, String description, String joined, String logo, String name) {
        this.creator = creator;
        this.description = description;
        this.joined = joined;
        this.logo = logo;
        this.name = name;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJoined() {
        return joined;
    }

    public void setJoined(String joined) {
        this.joined = joined;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
