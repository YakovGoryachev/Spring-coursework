package com.goryachev.music_service.DTO;


import com.goryachev.music_service.Pojo.Track;
import com.goryachev.music_service.Pojo.User;

import java.time.LocalDate;

public class CommentDto {
    public CommentDto(){

    }
    public CommentDto(int id, String text, int userId, int trackId, LocalDate commentDate){
        this.id = id;
        this.text = text;
        this.userId = userId;
        this.trackId = trackId;
        this.commentDate = commentDate;
    }
    private int id;
    private String text;
    private User user;
    private int userId;
    private Track track;
    private int trackId;
    private LocalDate commentDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public LocalDate getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(LocalDate commentDate) {
        this.commentDate = commentDate;
    }
}
