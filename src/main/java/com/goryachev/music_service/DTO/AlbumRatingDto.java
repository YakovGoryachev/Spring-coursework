package com.goryachev.music_service.DTO;

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
    
    public AlbumRatingDto(int id, Integer value, int userId, String userLogin, int albumId, String albumName, LocalDate ratingDate){
        this.id = id;
        this.value = value;
        this.userId = userId;
        this.userLogin = userLogin;
        this.albumId = albumId;
        this.albumName = albumName;
        this.ratingDate = ratingDate;
    }
    
    private int id;
    private Integer value;
    private int userId;
    private String userLogin;
    private int albumId;
    private String albumName;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public int getAlbumId() {
        return albumId;
    }
    
    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
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
