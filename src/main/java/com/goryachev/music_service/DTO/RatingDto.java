package com.goryachev.music_service.DTO;

import java.time.LocalDate;

public class RatingDto {
    public RatingDto(){

    }
    public RatingDto(int id, Integer value, int userId, int trackId, LocalDate ratingDate){
        this.id = id;
        this.value = value;
        this.userId = userId;
        this.trackId = trackId;
        this.ratingDate = ratingDate;
    }
    
    public RatingDto(int id, Integer value, int userId, String userLogin, int trackId, String trackName, LocalDate ratingDate){
        this.id = id;
        this.value = value;
        this.userId = userId;
        this.userLogin = userLogin;
        this.trackId = trackId;
        this.trackName = trackName;
        this.ratingDate = ratingDate;
    }
    
    private int id;
    private Integer value;
    private int userId;
    private String userLogin; // Для отображения в ответах
    private int trackId;
    private String trackName; // Для отображения в ответах
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

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public LocalDate getRatingDate() {
        return ratingDate;
    }

    public void setRatingDate(LocalDate ratingDate) {
        this.ratingDate = ratingDate;
    }
}
