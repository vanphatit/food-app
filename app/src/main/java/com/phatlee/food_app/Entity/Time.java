package com.phatlee.food_app.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "time")
public class Time implements Serializable {
    @PrimaryKey
    @SerializedName("Id")  // Match JSON key "Id" với biến "id"
    private int id;
    @SerializedName("Value")
    private String value;

    public Time(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() { return id; }
    public String getValue() { return value; }
}