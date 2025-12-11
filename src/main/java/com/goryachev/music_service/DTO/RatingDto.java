package com.goryachev.music_service.DTO;

import com.goryachev.music_service.Pojo.Track;
import com.goryachev.music_service.Pojo.User;
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
    private int id;
    private Integer value;
    private User user;
    private int userId;
    private Track track;
    private int trackId;
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

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public LocalDate getRatingDate() {
        return ratingDate;
    }

    public void setRatingDate(LocalDate ratingDate) {
        this.ratingDate = ratingDate;
    }
}
