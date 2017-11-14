package de.hdm.project.billtracker.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Scan {

    private String id;
    private String title;
    private String category;
    private long date;
    private Double sum;
    private String imageId;
    private String imagePath;
    private String imageData;

    public Scan(String category, long date, Double sum, String imagePath) {
        // setTitle(title);
        setDate(date);
        setCategory(category);
        setSum(sum);
        setImagePath(imagePath);
    }

    public Scan() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String printDate() {
        Date d = new Date(this.date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String date = dateFormat.format(d);
        return date;
    }

}
