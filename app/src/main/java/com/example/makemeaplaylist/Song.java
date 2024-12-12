package com.example.makemeaplaylist;

import androidx.annotation.NonNull;

public class Song {
    private String artist;
    private String name;

    public Song(String artist, String name) {
        this.name = name;
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        String output = this.name;
        if (!this.artist.trim().equals("")) {
            output = "\"" + output + "\" by " + this.artist;
        }
        return output;
    }
}
