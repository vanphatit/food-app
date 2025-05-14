package com.phatlee.food_app.Entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "wishlist",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = CASCADE),
                @ForeignKey(entity = Foods.class, parentColumns = "id", childColumns = "foodId", onDelete = CASCADE)
        })
public class Wishlist {

    private String id;
    private String userId;
    private int foodId;

    public Wishlist(String userId, int foodId) {
        this.userId = userId;
        this.foodId = foodId;
    }

    public Wishlist(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }
}
