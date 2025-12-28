package com.goryachev.music_service.DTO;

import java.sql.Date;
import java.time.LocalDateTime;

public class UserDto {
    public UserDto(){

    }

    public UserDto(int id, String login, String email, boolean isAdmin, LocalDateTime dateRegister, Date dateBorn) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.isAdmin = isAdmin;
        this.dateRegister = dateRegister;
        this.dateBorn = dateBorn;
    }

    private int id;
    private String login;
    private String email;
    private String password; // Используется только при создании/обновлении
    private boolean isAdmin;
    private LocalDateTime dateRegister;
    private Date dateBorn;

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getDateRegister() {
        return dateRegister;
    }

    public void setDateRegister(LocalDateTime dateRegister) {
        this.dateRegister = dateRegister;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateBorn() {
        return dateBorn;
    }

    public void setDateBorn(Date dateBorn) {
        this.dateBorn = dateBorn;
    }
}
