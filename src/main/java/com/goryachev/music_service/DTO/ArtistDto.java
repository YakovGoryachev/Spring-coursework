package com.goryachev.music_service.DTO;

import com.goryachev.music_service.Pojo.Artist;

public class ArtistDto {
    public ArtistDto(){

    }
    public ArtistDto(int id, String name){
        this.id = id;
        this.name = name;
    }

    private int id;
    private String name;
    private String avatarPath;

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
