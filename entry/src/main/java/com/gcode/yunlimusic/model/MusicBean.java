package com.gcode.yunlimusic.model;

import java.util.Locale;

public class MusicBean {
    private int id;
    private String title;
    private String song;
    private String data;
    private String duration;
    private String artist;
    private String album;

    public MusicBean() {
    }

    /**
     * @param id       music_id
     * @param title    music_name
     * @param song
     * @param data     music_path
     * @param duration music_duration
     * @param artist music_artist
     */
    public MusicBean(int id, String title, String song, String data, String duration,String artist,String album) {
        this.id = id;
        this.title = title;
        this.song = song;
        this.data = data;
        this.duration = duration;
        this.artist = artist;
        this.album = album;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}