package com.goryachev.music_service.DTO;

public class TrackDto {
    public TrackDto(){

    }
    public TrackDto(int id, String name, String artistName, String albumName,
                    String groupName, String genre, Integer duration, Integer playCount,
                    String avatarPath, String filePath){
        this.id = id;
        this.name =name;
        this.artistName = artistName;
        this.albumName = albumName;
        this.groupName = groupName;
        this.genre = genre;
        this.duration = duration;
        this.playCount = playCount;
        this.avatarPath = avatarPath;
        this.filePath = filePath;
    }
    private int id;
    private String name;
    private String artistName;
    private String albumName;
    private int albumId;
    private String groupName;
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

    public String getArtistName() {
        return artistName;
    }
    public String getFormattedDuration() {
        if (duration == null) return "0:00";
        int totalSeconds = duration.intValue();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
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
