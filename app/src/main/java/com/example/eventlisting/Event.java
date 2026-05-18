package com.example.eventlisting;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "events")
public class Event {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "capacity")
    private int capacity;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "city")
    private String city;

    @ColumnInfo(name = "district")
    private String district;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "isPaid")
    private boolean isPaid;

    @ColumnInfo(name = "imageResourceId")
    private int imageResourceId;

    @ColumnInfo(name = "imageUri")
    private String imageUri;

    @ColumnInfo(name = "createdBy")
    private String createdBy;


    // Constructor
    public Event(String name, String date, String description, int capacity, double latitude,
                 double longitude, String city, String district, String type, boolean isPaid,
                 int imageResourceId, String imageUri, String createdBy) {
        this.name = name;
        this.date = date;
        this.description = description;
        this.capacity = capacity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.district = district;
        this.type = type;
        this.isPaid = isPaid;
        this.imageResourceId = imageResourceId;
        this.imageUri = imageUri;
        this.createdBy = createdBy; // Artık parametre var
    }


    // Getter & Setter
    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getType() {
        return type;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getImageUri() { return imageUri; }
    public void setImageUri(String uri) { this.imageUri = uri; }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setType(String type) {
        this.type = type;
    }



}
