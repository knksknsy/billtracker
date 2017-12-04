package de.hdm.project.billtracker.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Bill implements Parcelable {

    private String id;
    private String title;
    private String category;
    private long date;
    private Double sum;
    private String imageId;
    private String imagePath;
    private String thumbnailPath;
    private String downloadUrl;

    public Bill(String title, String category, long date, Double sum, String imagePath, String thumbnailPath) {
        setTitle(title);
        setCategory(category);
        setDate(date);
        setSum(sum);
        setImagePath(imagePath);
        setThumbnailPath(thumbnailPath);
    }

    public Bill() {

    }

    /**
     * Constructor for parcel bill instantiation
     *
     * @param in
     */
    public Bill(Parcel in) {
        id = in.readString();
        title = in.readString();
        category = in.readString();
        date = in.readLong();
        sum = in.readDouble();
        imageId = in.readString();
        imagePath = in.readString();
        thumbnailPath = in.readString();
        downloadUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(category);
        dest.writeLong(date);
        dest.writeDouble(sum);
        dest.writeString(imageId);
        dest.writeString(imagePath);
        dest.writeString(thumbnailPath);
        dest.writeString(downloadUrl);
    }

    public static final Parcelable.Creator<Bill> CREATOR = new Parcelable.Creator<Bill>() {
        public Bill createFromParcel(Parcel in) {
            return new Bill(in);
        }

        public Bill[] newArray(int size) {
            return new Bill[size];
        }
    };

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

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String printDate() {
        Date d = new Date(this.date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String date = dateFormat.format(d);
        return date;
    }

}
