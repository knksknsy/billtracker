package de.hdm.project.billtracker;

import java.util.Calendar;

public class Scan {

    private int date;
    private String category;
    private Double sum;
    private String imagePath;

    public Scan(int date, String category, Double sum, String imagePath) {
        this.date = date;
        this.category = category;
        this.sum = sum;
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
