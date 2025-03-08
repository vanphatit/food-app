package com.phatlee.food_app.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "location")
public class Location implements Serializable {
    @PrimaryKey
    @SerializedName("Id")  // Match JSON key "Id" với biến "id"
    private int id;
    @SerializedName("loc")
    private String loc;

    public Location(int id, String loc) {
        this.id = id;
        this.loc = loc;
    }

    public int getId() { return id; }
    public String getLoc() { return loc; }
}
