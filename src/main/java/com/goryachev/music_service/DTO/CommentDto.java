package com.goryachev.music_service.DTO;

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
    
    public CommentDto(int id, String text, int userId, String userLogin, int trackId, String trackName, LocalDate commentDate){
        this.id = id;
        this.text = text;
        this.userId = userId;
        this.userLogin = userLogin;
        this.trackId = trackId;
        this.trackName = trackName;
        this.commentDate = commentDate;
    }
    
    private int id;
    private String text;
    private int userId;
    private String userLogin;
    private int trackId;
    private String trackName;
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
    
    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
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
