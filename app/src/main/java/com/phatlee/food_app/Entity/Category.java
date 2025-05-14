package com.phatlee.food_app.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "category")
public class Category implements Serializable {
    @PrimaryKey
    @SerializedName("Id")  // Match JSON key "Id" với biến "id"
    private int id;
    @SerializedName("ImagePath")  // Match JSON key "ImagePath" với biến "imagePath"
    private String imagePath;
    @SerializedName("Name")  // Match JSON key "Name" với biến "name"
    private String name;

    public Category(int id, String imagePath, String name) {
        this.id = id;
        this.imagePath = imagePath;
        this.name = name;
    }

    // Constructor không tham số – cần cho Firestore deserialization
    public Category() {
    }

    public int getId() { return id; }
    public String getImagePath() { return imagePath; }
    public String getName() { return name; }
}
