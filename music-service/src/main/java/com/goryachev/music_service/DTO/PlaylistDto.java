package com.goryachev.music_service.DTO;

import java.time.LocalDate;
import java.util.Date;

public class PlaylistDto {
    public PlaylistDto(){

    }
    public PlaylistDto(int id, String name, String description, Integer trackCount, LocalDate createdAt){
        this.id = id;
        this.name = name;
        this.description = description;
        this.trackCount = trackCount;
        this.createdAt = createdAt;
    }
    private int id;
    private String name;
    private String description;
    private Integer trackCount;
    private LocalDate createdAt;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(Integer trackCount) {
        this.trackCount = trackCount;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
