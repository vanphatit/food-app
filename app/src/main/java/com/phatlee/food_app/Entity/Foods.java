package com.phatlee.food_app.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "foods")
public class Foods implements Serializable {
    @PrimaryKey
    @SerializedName("Id")  // Match JSON key "Id" với biến "id"
    private int id;
    @SerializedName("CategoryId")  // Match JSON key "Id" với biến "id"
    private int categoryId;
    @SerializedName("Description")  // Match JSON key "Name" với biến "name"
    private String description;
    @SerializedName("BestFood")  // Match JSON key "Name" với biến "name"
    private boolean bestFood;
    @SerializedName("ImagePath")  // Match JSON key "Name" với biến "name"
    private String imagePath;
    @SerializedName("LocationId")  // Match JSON key "Name" với
    private int locationId;
    @SerializedName("Price")  // Match JSON key "Name" với biến "name"
    private double price;
    @SerializedName("PriceId")  // Match JSON key "Name" với biến "name"
    private int priceId;
    @SerializedName("Star")  // Match JSON key "Name" với biến "name"
    private double star;
    @SerializedName("TimeId")  // Match JSON key "Name" với biến "name"
    private int timeId;
    @SerializedName("TimeValue")  // Match JSON key "Name" với biến "name"
    private int timeValue;
    @SerializedName("Title")  // Match JSON key "Name" với biến "name"
    private String title;

    private int stockQuantity;

    public Foods(int id, int categoryId, String description, boolean bestFood, String imagePath, int locationId,
                 double price, int priceId, double star, int timeId, int timeValue, String title) {
        this.id = id;
        this.categoryId = categoryId;
        this.description = description;
        this.bestFood = bestFood;
        this.imagePath = imagePath;
        this.locationId = locationId;
        this.price = price;
        this.priceId = priceId;
        this.star = star;
        this.timeId = timeId;
        this.timeValue = timeValue;
        this.title = title;
        this.stockQuantity = 50;
    }

    // Constructor không tham số – cần cho Firestore deserialization
    public Foods() {
    }

    public int getId() { return id; }
    public int getCategoryId() { return categoryId; }
    public String getDescription() { return description; }
    public boolean isBestFood() { return bestFood; }
    public String getImagePath() { return imagePath; }
    public int getLocationId() { return locationId; }
    public double getPrice() { return price; }
    public int getPriceId() { return priceId; }
    public double getStar() { return star; }
    public int getTimeId() { return timeId; }
    public int getTimeValue() { return timeValue; }
    public String getTitle() { return title; }


    public void setId(int id) { this.id = id; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public void setDescription(String description) { this.description = description; }
    public void setBestFood(boolean bestFood) { this.bestFood = bestFood; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setLocationId(int locationId) { this.locationId = locationId; }
    public void setPrice(double price) { this.price = price; }
    public void setPriceId(int priceId) { this.priceId = priceId; }
    public void setStar(double star) { this.star = star; }
    public void setTimeId(int timeId) { this.timeId = timeId; }
    public void setTimeValue(int timeValue) { this.timeValue = timeValue; }
    public void setTitle(String title) { this.title = title; }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}