package de.hdm.project.billtracker;

import java.util.UUID;

public class User {

    private String email;
    private String id;

    public User() {

    }

    public User(String id, String email) {
        this.id = id;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
