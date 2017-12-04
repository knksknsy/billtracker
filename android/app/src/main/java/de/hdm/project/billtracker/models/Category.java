package de.hdm.project.billtracker.models;

public class Category {

    private String id;
    private String name;

    public Category(String name) {
        setName(name);
    }

    public Category() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
