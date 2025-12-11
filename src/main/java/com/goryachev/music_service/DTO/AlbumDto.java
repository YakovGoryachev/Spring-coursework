package com.goryachev.music_service.DTO;

import java.time.LocalDate;

public class AlbumDto {
    public AlbumDto(){

    }
    public AlbumDto(int id, String name, String groupName, Integer playCount){
        this.id = id;
        this.name = name;
        this.groupName = groupName;
        this.playCount = playCount;
    }
    private int id;
    private String name;
    private int groupId;
    private String groupName;
    private LocalDate releaseDate;
    private Integer playCount;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Integer playCount) {
        this.playCount = playCount;
    }
}
