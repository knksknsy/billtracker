package de.hdm.project.billtracker.models;


public class User {

    private String email;
    private String id;

    public User(String id, String email) {
        setId(id);
        setEmail(email);
    }

    public User() {

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
