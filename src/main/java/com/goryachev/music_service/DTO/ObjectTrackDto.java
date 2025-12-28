package com.goryachev.music_service.DTO;

import com.goryachev.music_service.Pojo.Album;
import com.goryachev.music_service.Pojo.Artist;
import com.goryachev.music_service.Pojo.Group;
import jakarta.persistence.criteria.CriteriaBuilder;

public class ObjectTrackDto {

    public ObjectTrackDto(){

    }

    public ObjectTrackDto(int id, String name, Artist artist, Album album, int albumId, Group group,
                          String genre, Integer duration, Integer playCount, String avatarPath,
                          String filePath, Double averageRating, Integer commentCount){
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.albumId = albumId;
        this.group = group;
        this.genre = genre;
        this.duration = duration;
        this.playCount = playCount;
        this.avatarPath = avatarPath;
        this.filePath = filePath;
        this.averageRating = averageRating;
        this.commentCount = commentCount;
    }
    public ObjectTrackDto(int id, String name, Artist artist, Album album, Group group,
                          String genre, Integer duration, Integer playCount, String avatarPath,
                          String filePath, Double averageRating, Integer commentCount){
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.group = group;
        this.genre = genre;
        this.duration = duration;
        this.playCount = playCount;
        this.avatarPath = avatarPath;
        this.filePath = filePath;
        this.averageRating = averageRating;
        this.commentCount = commentCount;
    }
    private int id;
    private String name;
    private Artist artist;
    private Album album;
    private int albumId; // ID альбома
    private Group group;
    private String genre;
    private Integer duration;
    private Integer playCount;
    private String avatarPath;
    private String filePath;
    private Double averageRating;
    private Integer commentCount;

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

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
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

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Integer playCount) {
        this.playCount = playCount;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
}
