package com.goryachev.music_service.DTO;

import com.goryachev.music_service.Pojo.Album;
import com.goryachev.music_service.Pojo.User;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.time.LocalDate;

public class AlbumRatingDto {
    public AlbumRatingDto(){

    }
    public AlbumRatingDto(int id, Integer value, int userId, int albumId, LocalDate ratingDate){
        this.id = id;
        this.value = value;
        this.userId = userId;
        this.albumId = albumId;
        this.ratingDate = ratingDate;
    }
    private int id;
    private Integer value;
    private User user;
    private int userId;
    private Album album;
    private int albumId;
    private LocalDate ratingDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public LocalDate getRatingDate() {
        return ratingDate;
    }

    public void setRatingDate(LocalDate ratingDate) {
        this.ratingDate = ratingDate;
    }
}
