package com.goryachev.music_service.DTO;

public class GroupDto {
    public GroupDto(){

    }
    public GroupDto(int id, String name, String avatarPath){
        this.id = id;
        this.name = name;
        this.avatarPath = avatarPath;
    }
    private int id;
    private String name;
    private String avatarPath;
    private Integer artistCount;
    private Integer albumCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getArtistCount() {
        return artistCount;
    }

    public void setArtistCount(Integer artistCount) {
        this.artistCount = artistCount;
    }

    public Integer getAlbumCount() {
        return albumCount;
    }

    public void setAlbumCount(Integer albumCount) {
        this.albumCount = albumCount;
    }
}
