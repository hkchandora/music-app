package com.himanshu.musicapp.model;

import java.io.Serializable;

public class MusicModel implements Serializable {

    String id;
    String title;
    String album;
    String artist;
    String genre;
    String source;
    String image;
    int trackNumber;
    int totalTrackCount;
    int duration;
    String site;

    public MusicModel() {
    }

    public MusicModel(String title, String artist, String source, String image, int duration) {
        this.title = title;
        this.artist = artist;
        this.source = source;
        this.image = image;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public int getTotalTrackCount() {
        return totalTrackCount;
    }

    public void setTotalTrackCount(int totalTrackCount) {
        this.totalTrackCount = totalTrackCount;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
